package com.web.ecommerce.service;

import com.web.ecommerce.dto.product.CreateProductRequest;
import com.web.ecommerce.model.Category;
import com.web.ecommerce.model.Product;
import com.web.ecommerce.model.ProductSize;
import com.web.ecommerce.model.Size;
import com.web.ecommerce.repository.CategoryRepository;
import com.web.ecommerce.repository.ProductRepository;
import com.web.ecommerce.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final SizeRepository sizeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(SizeRepository sizeRepository,
                          CategoryRepository categoryRepository,
                          ProductRepository productRepository){
        this.sizeRepository = sizeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Product addNewProduct(CreateProductRequest createProductRequest){
        Optional<Category> category = categoryRepository.findById(createProductRequest.getCategoryId());
        Optional<Size> size = sizeRepository.findById(createProductRequest.getProductSizeId());
        if(category.isPresent() && size.isPresent()){
            Product newProduct = Product
                    .builder()
                    .name(createProductRequest.getName())
                    .description(createProductRequest.getDescription())
                    .price(createProductRequest.getPrice())
                    .category(category.get())
                    .build();
            ProductSize productSize = ProductSize
                    .builder()
                    .product(newProduct)
                    .size(size.get())
                    .stockCount(createProductRequest.getStockCount()).build();
            List<ProductSize> sizes = new ArrayList<>();
            sizes.add(productSize);
            newProduct.setSizes(sizes);
            productRepository.save(newProduct);
            return newProduct;
        }
        return null;
    }

    public Product getProduct(Long id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            return product.get();
        }
        throw new RuntimeException();
    }

    @Transactional
    public Product updateProduct(Long id,Product newProduct){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            Product updatedProduct = product.get();
            updatedProduct.setName(newProduct.getName());
            updatedProduct.setDescription(newProduct.getDescription());
            updatedProduct.setPrice(newProduct.getPrice());
            updatedProduct.setCategory(newProduct.getCategory());
            updatedProduct.setSizes(newProduct.getSizes());
            if(newProduct.getImages()!=null){
                updatedProduct.setImages(newProduct.getImages());
            }
            productRepository.save(updatedProduct);
            return updatedProduct;
        }
        throw new RuntimeException();
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public void addNewCategory(Category category){
        categoryRepository.save(category);
    }

    public List<Category> getCategories(){
        return categoryRepository.findAll();
    }

    public void addNewSize(Size size){
        sizeRepository.save(size);
    }

    public List<Size> getSizes(){
        return sizeRepository.findAll();
    }

}
