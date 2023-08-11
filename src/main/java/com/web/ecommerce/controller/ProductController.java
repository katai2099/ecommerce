package com.web.ecommerce.controller;

import com.web.ecommerce.dto.product.CreateProductRequest;
import com.web.ecommerce.model.Category;
import com.web.ecommerce.model.Product;
import com.web.ecommerce.model.Size;
import com.web.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping("/category/")
    public ResponseEntity<String> addNewCategory(@RequestBody Category category){
        productService.addNewCategory(category);
        return new ResponseEntity<>("Category created", HttpStatus.CREATED);
    }

    @GetMapping("/category/")
    public ResponseEntity<List<Category>> getCategories(){
        return new ResponseEntity<>(productService.getCategories(),HttpStatus.OK);
    }

    @PostMapping("/size/")
    public ResponseEntity<String> addNewSize(@RequestBody Size size){
        productService.addNewSize(size);
        return new ResponseEntity<>("Size created",HttpStatus.CREATED);
    }

    @GetMapping("/size/")
    public ResponseEntity<List<Size>> getSizes(){
        return new ResponseEntity<>(productService.getSizes(),HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Product> addNewProduct(@RequestBody CreateProductRequest createProductRequest){
        Product product = productService.addNewProduct(createProductRequest);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId){
        Product product = productService.getProduct(productId);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                                 @RequestBody Product product){
        Product updatedProduct = productService.updateProduct(productId,product);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
        return new ResponseEntity<>("Product with ID " + productId + " removed",HttpStatus.OK);
    }



}
