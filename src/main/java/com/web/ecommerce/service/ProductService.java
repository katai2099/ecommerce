package com.web.ecommerce.service;

import com.web.ecommerce.configuration.aws.s3.S3Buckets;
import com.web.ecommerce.configuration.aws.s3.S3Service;
import com.web.ecommerce.dto.PaginationResponse;
import com.web.ecommerce.dto.product.*;
import com.web.ecommerce.enumeration.Gender;
import com.web.ecommerce.exception.FailUploadImageException;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.product.*;
import com.web.ecommerce.repository.CategoryRepository;
import com.web.ecommerce.repository.ProductRepository;
import com.web.ecommerce.repository.SizeRepository;
import com.web.ecommerce.specification.product.ProductFilter;
import com.web.ecommerce.specification.product.ProductSpecificationBuilder;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.web.ecommerce.util.Constant.*;

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


    public PaginationResponse<ProductDTO> getProducts(ProductFilter filter) {
        Pageable pageable = buildProductPageable(filter);
        ProductSpecificationBuilder builder = new ProductSpecificationBuilder();
        builder.withFilter(filter);
        Specification<Product> spec = builder.build();
        Page<Product> products = productRepository.findAll(spec, pageable);
        return PaginationResponse.<ProductDTO>builder()
                .currentPage(filter.getPage())
                .totalPage(products.getTotalPages())
                .totalItem(products.getTotalElements())
                .data(ProductDTO.toProductDTOs(products.toList()))
                .build();
    }

    private Pageable buildProductPageable(ProductFilter filter) {
        if (filter.getSort() != null) {
            String sort = filter.getSort().toUpperCase();
            switch (sort) {
                case HIGHEST_PRICE -> {
                    filter.setStock(DEFAULT);
                    return PageRequest.of(filter.getPage() - 1, filter.getItemperpage(), Sort.by("price").descending());
                }
                case LOWEST_PRICE -> {
                    filter.setStock(DEFAULT);
                    return PageRequest.of(filter.getPage() - 1, filter.getItemperpage(), Sort.by("price").ascending());
                }
                case NEWEST -> {
                    filter.setStock(DEFAULT);
                    return PageRequest.of(filter.getPage() - 1, filter.getItemperpage(), Sort.by("createdAt").descending());
                }
                case DEFAULT -> {
                    filter.setStock(DEFAULT);
                    return PageRequest.of(filter.getPage() - 1, filter.getItemperpage());
                }
            }
        }
        return PageRequest.of(filter.getPage() - 1, filter.getItemperpage());
    }

    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return ProductDTO.toProductDTO(product);
    }

    @Transactional
    public ProductDTO addNewProduct(CreateProductRequest createProductRequest, List<MultipartFile> files) {
        Category category = categoryRepository.findById(createProductRequest.getCategory().getId())
                .orElseThrow(() -> new InvalidContentException("The provided category does not exist. Please provide a valid category."));
        Product newProduct = Product.builder()
                .name(createProductRequest.getName())
                .description(createProductRequest.getDescription())
                .gender(Gender.valueOf(createProductRequest.getGender()))
                .price(createProductRequest.getPrice())
                .publish(createProductRequest.getPublish())
                .isFeatured(createProductRequest.getFeatured())
                .category(category)
                .build();
        List<ProductSize> productSizes = new ArrayList<>();
        for (int i = 0; i < createProductRequest.getProductSizes().size(); i++) {
            if (createProductRequest.getProductSizes().get(i).getStockCount() > 0) {
                Size size = sizeRepository.findByName(createProductRequest.getProductSizes().get(i).getSize().getName())
                        .orElseThrow(() -> new InvalidContentException("The provided size does not exist. Please provide a valid size."));
                ProductSize productSize = ProductSize.builder()
                        .product(newProduct)
                        .size(size)
                        .stockCount(createProductRequest.getProductSizes().get(i).getStockCount())
                        .build();
                productSizes.add(productSize);
            }
        }
        Collections.sort(productSizes);
        newProduct.setProductSizes(productSizes);
        List<String> urls = uploadProductImages(files, newProduct);
        createProductImages(urls, newProduct);
        Product prod = productRepository.save(newProduct);
        return ProductDTO.toProductDTO(prod);
    }

    @Transactional
    public CategoryDTO updateCategory(Category categoryRequest, List<MultipartFile> files) {
        if (files == null && categoryRequest.getCategoryImage().isEmpty()) {
            throw new InvalidContentException("Category need at least one image");
        }
        Category category = categoryRepository.findById(categoryRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryRequest.getId() + " does not exist"));
        if (categoryRequest.getName() != null && !categoryRequest.getName().isEmpty()) {
            category.setName(categoryRequest.getName());
        }
        if (categoryRequest.getIsTop() != null) {
            category.setIsTop(categoryRequest.getIsTop());
        }
        if (categoryRequest.getPublish() != null) {
            category.setPublish(categoryRequest.getPublish());
        }
        category.setLastModified(LocalDateTime.now());
        if (categoryRequest.getCategoryImage().isEmpty()) {
            category.setCategoryImage("");
        }
        if (files != null) {
            List<String> urls = uploadCategoryImage(files, category);
            createCategoryImage(urls, category);
        }
        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.toCategoryDTO(savedCategory);
    }

    private void createProductImages(List<String> urls, Product product) {
        for (String url : urls) {
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(url)
                    .build();
            product.getImages().add(image);
        }
    }

    private void createCategoryImage(List<String> urls, Category category) {
        for (String url : urls) {
            category.setCategoryImage(url);
        }
    }

    private List<String> uploadCategoryImage(List<MultipartFile> files, Category category) {
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            try {
                String key = "categories/%s_%s_%s".formatted(LocalDateTime.now().toString(), i + 1, category.getName());
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

    private List<String> uploadProductImages(List<MultipartFile> files, Product product) {
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            try {
                String key = "products/%s_%s_%s".formatted(LocalDateTime.now().toString(), i + 1, product.getName());
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
    public ProductDTO updateProduct(CreateProductRequest productRequest, List<MultipartFile> files) {
        Product product = productRepository.findById(productRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productRequest.getId() + " does not exist"));

        if (productRequest.getName() != null && !productRequest.getName().isEmpty()) {
            product.setName(productRequest.getName());
        }
        if (productRequest.getDescription() != null && !productRequest.getDescription().isEmpty()) {
            product.setDescription(productRequest.getDescription());
        }
        if (!Objects.equals(productRequest.getCategory().getId(), product.getCategory().getId())) {
            Category category = categoryRepository.findById(productRequest.getCategory().getId())
                    .orElseThrow(() -> new InvalidContentException("The provided category does not exist. Please provide a valid category."));
            product.setCategory(category);
        }
        if (productRequest.getGender() != null) {
            product.setGender(Gender.valueOf(productRequest.getGender()));
        }
        if (productRequest.getFeatured() != null) {
            product.setFeatured(productRequest.getFeatured());
        }
        if (productRequest.getPublish() != null) {
            product.setPublish(productRequest.getPublish());
        }
        product.setPrice(productRequest.getPrice());
        Set<Long> updatedSizeIds = productRequest.getProductSizes().stream()
                .map(ProductSizeDTO::getId)
                .collect(Collectors.toSet());
        product.getProductSizes().removeIf(size -> !updatedSizeIds.contains(size.getId()));
        for (ProductSizeDTO productSizeDto : productRequest.getProductSizes()) {
            if (productSizeDto.getId() == null || productSizeDto.getId() == 0) {
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
        Set<String> updatedImages = productRequest.getImages().stream()
                .map(ProductImageDTO::getImageUrl)
                .collect(Collectors.toSet());
        for (int i = 0; i < product.getImages().size(); i++) {
            ProductImage img = product.getImages().get(i);
            if (!updatedImages.contains(img.getImageUrl())) {
//                String key = img.getImageUrl().substring(img.getImageUrl().indexOf(".com") + 5);
//                s3Service.deleteObject(key);
                product.removeImage(img);
            }
        }
        if (files != null) {
            List<String> urls = uploadProductImages(files, product);
            createProductImages(urls, product);
        }
        Product prod = productRepository.save(product);
        return ProductDTO.toProductDTO(prod);
    }

    @Transactional
    public void setProductFeatured(Long id, Boolean featured) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setFeatured(featured);
        productRepository.save(product);
    }

    @Transactional
    public void setProductPublish(Long id, Boolean publish) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setPublish(publish);
        productRepository.save(product);
    }

    @Transactional
    public CategoryDTO addNewCategory(Category categoryData, List<MultipartFile> files) {
        Category category = new Category();
        category.setName(categoryData.getName());
        category.setIsTop(categoryData.getIsTop());
        category.setPublish(categoryData.getPublish());
        category.setLastModified(LocalDateTime.now());
        List<String> urls = uploadCategoryImage(files, category);
        createCategoryImage(urls, category);
        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.toCategoryDTO(savedCategory);
    }

    @Transactional
    public List<CategoryDTO> getCategories() {
        List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return CategoryDTO.toCategoryDTOS(categories);
    }

    @Transactional
    public SizeDTO addNewSize(SizeDTO sizeRequest) {
        Size size = new Size();
        size.setName(sizeRequest.getName());
        size.setPublish(sizeRequest.getPublish());
        size.setLastModified(LocalDateTime.now());
        Size savedSize = sizeRepository.save(size);
        return SizeDTO.toSizeDTO(savedSize);
    }

    public List<SizeDTO> getSizes() {
        List<Size> sizes = sizeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return SizeDTO.toSizeDTOS(sizes);
    }

    public List<ProductDTO> getFeaturedProducts() {
        List<Product> featuredProducts = productRepository.findAllByIsFeaturedIsTrue();
        return ProductDTO.toProductDTOs(featuredProducts);
    }

    public List<CategoryDTO> getTopCategories() {
        List<Category> topCategories = categoryRepository.findAllByIsTopIsTrue();
        return CategoryDTO.toCategoryDTOS(topCategories);
    }


    @Transactional
    public void setProductTop(Long categoryId, Boolean isTop) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category is not found"));
        category.setIsTop(isTop);
        categoryRepository.save(category);
    }

    @Transactional
    public void setCategoryPublish(Long categoryId, Boolean publish) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category is not found"));
        category.setPublish(publish);
        categoryRepository.save(category);
    }

    @Transactional
    public SizeDTO updateSize(SizeDTO size) {
        Size existingSize = sizeRepository.findById(size.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Size not found"));
        if (size.getPublish() != null) {
            existingSize.setPublish(size.getPublish());
        }
        if (size.getName() != null && !size.getName().isEmpty()) {
            existingSize.setName(size.getName());
        }
        existingSize.setLastModified(LocalDateTime.now());
        Size savedSize = sizeRepository.save(existingSize);
        return SizeDTO.toSizeDTO(savedSize);
    }
}
