package com.web.ecommerce.service;

import com.web.ecommerce.dto.product.CreateProductRequest;
import com.web.ecommerce.dto.product.ProductDTO;
import com.web.ecommerce.dto.product.ProductDetailDTO;
import com.web.ecommerce.enumeration.Gender;
import com.web.ecommerce.model.product.Category;
import com.web.ecommerce.model.product.Product;
import com.web.ecommerce.model.product.ProductSize;
import com.web.ecommerce.model.product.Size;
import com.web.ecommerce.repository.CategoryRepository;
import com.web.ecommerce.repository.ProductRepository;
import com.web.ecommerce.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final SizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(SizeRepository sizeRepository,
                          CategoryRepository categoryRepository,
                          ProductRepository productRepository) {
        this.sizeRepository = sizeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }


    public List<ProductDTO> getProducts(String gender,int page,String category){
        Pageable pageable =  PageRequest.of(page-1,20);
        List<Product> products = productRepository.findAllByGender(Gender.valueOf(gender),pageable);
        return ProductDTO.toProductDTOS(products);
    }

    public ProductDetailDTO getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found"));
        return ProductDetailDTO.productDetailDTO(product);
    }

    public List<ProductDTO> searchProducts(String gender, int page, String searchTerm) {
        Pageable pageable = PageRequest.of(page-1,20);
        List<Product> products = productRepository.findAllByNameContainingIgnoreCaseAndGender(searchTerm,Gender.valueOf(gender),pageable);
        return ProductDTO.toProductDTOS(products);
    }


    @Transactional
    public Product addNewProduct(CreateProductRequest createProductRequest) {
        Optional<Category> optionalCategory = categoryRepository.findById(createProductRequest.getCategory().getId());
        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("Category does not exist!");
        }
        Product newProduct = Product.builder()
                .name(createProductRequest.getName())
                .description(createProductRequest.getDescription())
                .gender(Gender.valueOf(createProductRequest.getGender()))
                .price(createProductRequest.getPrice())
                .category(optionalCategory.get())
                .build();

        for (int i = 0; i < createProductRequest.getSizes().size(); i++) {
            Optional<Size> optionalSize = sizeRepository.findByName(createProductRequest.getSizes().get(i).getSize().getName());
            if (optionalSize.isEmpty()) {
                throw new RuntimeException("Size does not exist!");
            }
            ProductSize productSize = ProductSize.builder()
                    .product(newProduct)
                    .size(optionalSize.get())
                    .stockCount(createProductRequest.getSizes().get(i).getStockCount())
                    .build();
            newProduct.getProductSizes().add(productSize);
        }
        return productRepository.save(newProduct);
    }

    @Transactional
    public void updateProduct(CreateProductRequest dto) {
        Product product = productRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Product not found"));

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            product.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            product.setDescription(dto.getDescription());
        }
        if (!Objects.equals(dto.getCategory().getId(), product.getCategory().getId())) {
            Category category = categoryRepository.findById(dto.getCategory().getId()).orElseThrow(() -> new RuntimeException("No category"));
            product.setCategory(category);
        }
        if(dto.getGender() != null){
            product.setGender(Gender.valueOf(dto.getGender()));
        }
        product.setPrice(dto.getPrice());
        Set<Long> updatedSizeIds = dto.getSizes().stream()
                .map(CreateProductRequest.ProductSize::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        product.getProductSizes().removeIf(size -> !updatedSizeIds.contains(size.getId()));

        for (CreateProductRequest.ProductSize productSizeDto : dto.getSizes()) {
            if (productSizeDto.getId() == null) {
                Size size = sizeRepository.findByName(productSizeDto.getSize().getName()).orElseThrow(() -> new RuntimeException("Size does not exist"));
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
                        .orElseThrow(() -> new RuntimeException("Size not found"));
                existingSize.setStockCount(productSizeDto.getStockCount());
            }
        }
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void addNewCategory(Category category) {
        categoryRepository.save(category);
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public void addNewSize(Size size) {
        sizeRepository.save(size);
    }

    public List<Size> getSizes() {
        return sizeRepository.findAll();
    }

}
