package com.web.ecommerce.service;

import com.web.ecommerce.configuration.aws.s3.S3Buckets;
import com.web.ecommerce.configuration.aws.s3.S3Service;
import com.web.ecommerce.dto.product.CreateProductRequest;
import com.web.ecommerce.dto.product.ProdDTO;
import com.web.ecommerce.dto.product.ProductDTO;
import com.web.ecommerce.dto.product.ProductDetailDTO;
import com.web.ecommerce.enumeration.Gender;
import com.web.ecommerce.exception.FailUploadImageException;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.product.*;
import com.web.ecommerce.repository.CategoryRepository;
import com.web.ecommerce.repository.ProductRepository;
import com.web.ecommerce.repository.SizeRepository;
import com.web.ecommerce.specification.ProductFilter;
import com.web.ecommerce.specification.ProductSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final SizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    @Autowired
    public ProductService(SizeRepository sizeRepository,
                          CategoryRepository categoryRepository,
                          ProductRepository productRepository, S3Service s3Service, S3Buckets s3Buckets) {
        this.sizeRepository = sizeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }


    @Transactional
    public List<ProdDTO> getProducts(ProductFilter filter) {
        ProductSpecificationBuilder builder = new ProductSpecificationBuilder();
        builder.withFilter(filter);
        Specification<Product> spec = builder.build();
        Pageable pageable = PageRequest.of(filter.getPage() - 1, 20);
        Page<Product> products = productRepository.findAll(spec,pageable);
        return ProdDTO.toProdDTOS(products.toList());
    }

    @Transactional
    public ProductDetailDTO getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return ProductDetailDTO.productDetailDTO(product);
    }

    @Transactional
    public List<ProductDTO> searchProducts(String gender, int page, String searchTerm) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        List<Product> products = productRepository.findAllByNameContainingIgnoreCaseAndGender(searchTerm, Gender.valueOf(gender), pageable);
        return ProductDTO.toProductDTOS(products);
    }


    @Transactional
    public Product addNewProduct(CreateProductRequest createProductRequest, List<MultipartFile> files) {

        Category category = categoryRepository.findById(createProductRequest.getCategory().getId())
                .orElseThrow(() -> new InvalidContentException("The provided category does not exist. Please provide a valid category."));
        Product newProduct = Product.builder()
                .name(createProductRequest.getName())
                .description(createProductRequest.getDescription())
                .gender(Gender.valueOf(createProductRequest.getGender()))
                .price(createProductRequest.getPrice())
                .category(category)
                .build();
        for (int i = 0; i < createProductRequest.getProductSizes().size(); i++) {
            if(createProductRequest.getProductSizes().get(i).getStockCount()>0){
                Size size = sizeRepository.findByName(createProductRequest.getProductSizes().get(i).getSize().getName())
                        .orElseThrow(() -> new InvalidContentException("The provided size does not exist. Please provide a valid size."));
                ProductSize productSize = ProductSize.builder()
                        .product(newProduct)
                        .size(size)
                        .stockCount(createProductRequest.getProductSizes().get(i).getStockCount())
                        .build();
                newProduct.getProductSizes().add(productSize);
            }
        }
        List<String> urls = uploadProductImages(files, newProduct);
        createProductImages(urls,newProduct);
        return productRepository.save(newProduct);
    }

    private void createProductImages(List<String> urls,Product product){
        for (String url : urls) {
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(url)
                    .build();
            product.getImages().add(image);
        }
    }

    private List<String> uploadProductImages(List<MultipartFile> files, Product product) {
        List<String> urls = new ArrayList<>();
        for (int i=0;i<files.size();i++) {
            try {
                String key = "products/%s_%s_%s".formatted(LocalDateTime.now().toString(),i+1, product.getName());
                s3Service.putObject("%s".formatted(key),
                        files.get(i).getBytes());
                String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
                urls.add("https://%s.s3.eu-north-1.amazonaws.com/%s".formatted(s3Buckets.getCustomer(), encodedKey));
            } catch (IOException e) {
                throw new FailUploadImageException();
            }
        }
        return urls;
    }

    @Transactional
    public void updateProduct(CreateProductRequest dto, List<MultipartFile> files) {
        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new InvalidContentException("Product with id " + dto.getId() + " does not exist"));

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            product.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            product.setDescription(dto.getDescription());
        }
        if (!Objects.equals(dto.getCategory().getId(), product.getCategory().getId())) {
            Category category = categoryRepository.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new InvalidContentException("The provided category does not exist. Please provide a valid category."));
            product.setCategory(category);
        }
        if (dto.getGender() != null) {
            product.setGender(Gender.valueOf(dto.getGender()));
        }
        product.setPrice(dto.getPrice());
        Set<Long> updatedSizeIds = dto.getProductSizes().stream()
                .map(CreateProductRequest.ProductSize::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        product.getProductSizes().removeIf(size -> !updatedSizeIds.contains(size.getId()));
        for (CreateProductRequest.ProductSize productSizeDto : dto.getProductSizes()) {
            if (productSizeDto.getId() == null) {
                Size size = sizeRepository.findByName(productSizeDto.getSize().getName())
                        .orElseThrow(() -> new InvalidContentException("The provided size does not exist. Please provide a valid size."));
                ProductSize productSize = ProductSize.builder()
                        .product(product)
                        .size(size)
                        .stockCount(productSizeDto.getStockCount())
                        .build();
                product.getProductSizes().add(productSize);
            } else {
                ProductSize existingSize = product.getProductSizes().stream()
                        .filter(size -> size.getId().equals(productSizeDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new InvalidContentException("The provided size does not exist. Please provide a valid size."));
                existingSize.setStockCount(productSizeDto.getStockCount());
            }
        }

        Set<String> updatedImages = dto.getImages().stream()
                .map(CreateProductRequest.Image::getImageUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        product.getImages().forEach((img) -> {
            if (!updatedImages.contains(img.getImageUrl())) {
                String key = img.getImageUrl().substring(img.getImageUrl().indexOf(".com") + 5);
                s3Service.deleteObject(key);
                product.getImages().remove(img);
            }
        });
        List<String> urls = uploadProductImages(files, product);
        createProductImages(urls,product);
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void addNewCategory(Category category) {
        categoryRepository.save(category);
    }

    @Transactional
    public List<Category> getCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    @Transactional

    public void addNewSize(Size size) {
        sizeRepository.save(size);
    }

    @Transactional

    public List<Size> getSizes() {
        return sizeRepository.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

}
