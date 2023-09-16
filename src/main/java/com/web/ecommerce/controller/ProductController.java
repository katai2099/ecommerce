package com.web.ecommerce.controller;

import com.web.ecommerce.dto.PaginationResponse;
import com.web.ecommerce.dto.product.*;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.model.product.Category;
import com.web.ecommerce.model.product.Size;
import com.web.ecommerce.service.ProductService;
import com.web.ecommerce.service.ReviewService;
import com.web.ecommerce.specification.ProductFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;


    @Autowired
    public ProductController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
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
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String gender,
                                                           @RequestParam String searchTerm,
                                                           @RequestParam int page) {
        List<ProductDTO> products = productService.searchProducts(gender.toUpperCase(), page, searchTerm);
        return ResponseEntity.ok(products);
    }


    @GetMapping()
    public ResponseEntity<PaginationResponse<ProductDTO>> getProducts(@ModelAttribute ProductFilter filter) {
        return ResponseEntity.ok(productService.getProducts(filter));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long productId) {
        ProductDTO product = productService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductDTO> addNewProduct(@RequestPart(name = "productData") CreateProductRequest productData,
                                                    @RequestPart List<MultipartFile> files) {
        ProductDTO product = productService.addNewProduct(productData, files);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{productId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,
                                                @RequestPart CreateProductRequest productData,
                                                @RequestPart(required = false, name = "files[]") List<MultipartFile> files) {
        if (!productId.equals(productData.getId())) {
            throw new InvalidContentException("Product ID mismatch");
        }
        productService.updateProduct(productData, files);
        return ResponseEntity.ok("Product updated successfully.");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>("Product with ID " + productId + " removed", HttpStatus.OK);
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ProductReviewDTO> getReviews(@PathVariable Long productId,
                                                       @RequestParam(required = false, defaultValue = "1") Integer page) {
        ProductReviewDTO productReviewDTO = reviewService.getReviews(productId, page);
        return ResponseEntity.ok(productReviewDTO);
    }

    @PutMapping("/{productId}/reviews")
    public ResponseEntity<String> submitReview(@PathVariable Long productId,
                                               @RequestBody NewReview newReview) {
        reviewService.saveReview(productId, newReview);
        return ResponseEntity.ok("Successfully submitted");
    }


}
