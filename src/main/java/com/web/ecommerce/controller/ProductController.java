package com.web.ecommerce.controller;

import com.web.ecommerce.dto.PaginationResponse;
import com.web.ecommerce.dto.product.*;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.model.ProductAttributeRequest;
import com.web.ecommerce.model.product.Category;
import com.web.ecommerce.service.ProductService;
import com.web.ecommerce.service.ReviewService;
import com.web.ecommerce.specification.product.ProductFilter;
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

    @PostMapping(value = "/category", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CategoryDTO> addNewCategory(@RequestPart(name = "categoryData") Category category,
                                                      @RequestPart List<MultipartFile> files) {
        CategoryDTO categoryDTO = productService.addNewCategory(category, files);
        return ResponseEntity.ok(categoryDTO);
    }

    @PutMapping(value = "/category/{categoryId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId,
                                                      @RequestPart(name = "categoryData") Category category,
                                                      @RequestPart(required = false) List<MultipartFile> files) {
        if (!categoryId.equals(category.getId())) {
            throw new InvalidContentException("Product ID mismatch");
        }
        CategoryDTO categoryDTO = productService.updateCategory(category, files);
        return ResponseEntity.ok(categoryDTO);
    }

    @PutMapping(value = "/category/{categoryId}/top-category")
    public ResponseEntity<String> setTopCategory(@PathVariable Long categoryId,
                                                 @RequestBody ProductAttributeRequest.CategoryIsTopRequest request) {
        productService.setCategoryTop(categoryId, request.getIsTop());
        return ResponseEntity.ok("Category successfully updated");
    }

    @PutMapping(value = "/category/{categoryId}/publish")
    public ResponseEntity<String> setCategoryPublish(@PathVariable Long categoryId,
                                                     @RequestBody ProductAttributeRequest.ProductPublishRequest request) {
        productService.setCategoryPublish(categoryId, request.getPublish());
        return ResponseEntity.ok("Category successfully updated");
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(productService.getCategories());
    }

    @PostMapping("/size")
    public ResponseEntity<SizeDTO> addNewSize(@RequestBody SizeDTO size) {
        SizeDTO sizeDTO = productService.addNewSize(size);
        return ResponseEntity.ok(sizeDTO);
    }

    @PutMapping("/size/{sizeId}")
    public ResponseEntity<SizeDTO> updateSize(@PathVariable Long sizeId,
                                             @RequestBody SizeDTO size) {
        if (!sizeId.equals(size.getId())) {
            throw new InvalidContentException("Id mismatch");
        }
        SizeDTO sizeDTO = productService.updateSize(size);
        return ResponseEntity.ok(sizeDTO);
    }

    @GetMapping("/size")
    public ResponseEntity<List<SizeDTO>> getSizes() {
        List<SizeDTO> sizeDTOS = productService.getSizes();
        return ResponseEntity.ok(sizeDTOS);
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
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,
                                                    @RequestPart CreateProductRequest productData,
                                                    @RequestPart(required = false) List<MultipartFile> files) {
        if (!productId.equals(productData.getId())) {
            throw new InvalidContentException("Product ID mismatch");
        }
        ProductDTO product = productService.updateProduct(productData, files);
        return ResponseEntity.ok(product);
    }

    @PutMapping(value = "/{productId}/featured")
    public ResponseEntity<String> setProductFeatured(@PathVariable Long productId,
                                                     @RequestBody ProductAttributeRequest.ProductFeaturedRequest request) {
        productService.setProductFeatured(productId, request.getFeatured());
        return ResponseEntity.ok("Product successfully updated");
    }

    @PutMapping(value = "/{productId}/publish")
    public ResponseEntity<String> setProductPublish(@PathVariable Long productId,
                                                    @RequestBody ProductAttributeRequest.ProductPublishRequest request) {
        productService.setProductPublish(productId, request.getPublish());
        return ResponseEntity.ok("Product successfully updated");
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<PaginationResponse<ReviewDTO>> getReviews(@PathVariable Long productId,
                                                                    @RequestParam(required = false, defaultValue = "1") Integer page) {
        PaginationResponse<ReviewDTO> response = reviewService.getReviews(productId, page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/user-review")
    public ResponseEntity<ReviewDTO> getUserReview(@PathVariable Long productId) {
        ReviewDTO reviewDTO = reviewService.getUserReview(productId);
        return ResponseEntity.ok(reviewDTO);
    }

    @DeleteMapping("/user-review/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/reviews")
    public ResponseEntity<String> submitReview(@PathVariable Long productId,
                                               @RequestBody NewReview newReview) {
        Long id = reviewService.saveReview(productId, newReview);
        return ResponseEntity.ok(id.toString());
    }

    @GetMapping("/featured-products")
    public ResponseEntity<List<ProductDTO>> getFeaturedProducts() {
        List<ProductDTO> res = productService.getFeaturedProducts();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/top-categories")
    public ResponseEntity<List<CategoryDTO>> getTopCategories() {
        List<CategoryDTO> res = productService.getTopCategories();
        return ResponseEntity.ok(res);
    }


}
