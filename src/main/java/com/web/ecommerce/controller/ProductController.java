package com.web.ecommerce.controller;

import com.web.ecommerce.dto.product.CreateProductRequest;
import com.web.ecommerce.dto.product.ProductDTO;
import com.web.ecommerce.dto.product.ProductDetailDTO;
import com.web.ecommerce.model.product.Category;
import com.web.ecommerce.model.product.Product;
import com.web.ecommerce.model.product.Size;
import com.web.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/category/")
    public ResponseEntity<String> addNewCategory(@RequestBody Category category) {
        productService.addNewCategory(category);
        return new ResponseEntity<>("Category created", HttpStatus.CREATED);
    }

    @GetMapping("/category/")
    public ResponseEntity<List<Category>> getCategories() {
        return new ResponseEntity<>(productService.getCategories(), HttpStatus.OK);
    }

    @PostMapping("/size/")
    public ResponseEntity<String> addNewSize(@RequestBody Size size) {
        productService.addNewSize(size);
        return new ResponseEntity<>("Size created", HttpStatus.CREATED);
    }

    @GetMapping("/size/")
    public ResponseEntity<List<Size>> getSizes() {
        return new ResponseEntity<>(productService.getSizes(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>>searchProducts(@RequestParam String section,
                                                          @RequestParam String searchTerm,
                                                          @RequestParam int page){
        List<ProductDTO> products = productService.searchProducts(section.toUpperCase(),page,searchTerm);
        return ResponseEntity.ok(products);
    }


    @GetMapping()
    public ResponseEntity<List<ProductDTO>> getProducts(@RequestParam String section,
                                                        @RequestParam int page,
                                                        @RequestParam(required = false) String category) {
        List<ProductDTO> products =  productService.getProducts(section.toUpperCase(),page,category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailDTO> getProduct(@PathVariable Long productId) {
        ProductDetailDTO product = productService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/")
    public ResponseEntity<Product> addNewProduct(@RequestBody CreateProductRequest createProductRequest) {
        Product product = productService.addNewProduct(createProductRequest);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,
                                                @RequestBody CreateProductRequest product) {
        if (!productId.equals(product.getId())) {
            return ResponseEntity.badRequest().body("Product ID mismatch.");
        }
        productService.updateProduct(product);
        return ResponseEntity.ok("Product updated successfully.");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>("Product with ID " + productId + " removed", HttpStatus.OK);
    }


}
