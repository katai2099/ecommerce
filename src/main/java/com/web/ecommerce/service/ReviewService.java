package com.web.ecommerce.service;

import com.web.ecommerce.dto.product.NewReview;
import com.web.ecommerce.dto.product.ReviewDTO;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.product.Product;
import com.web.ecommerce.model.product.Review;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.ProductRepository;
import com.web.ecommerce.repository.ReviewRepository;
import com.web.ecommerce.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewDTO getReviews(Long productId, int page) {
        Map<String, Object> response = new HashMap<>();
        Pageable pageable;
        ReviewDTO dto = new ReviewDTO();
        if (page == 1) {
            Optional<Review> ownerReview = reviewRepository.findByProductIdAndUserId(productId, 1L);
            dto.setOwnerReview( ownerReview.orElse(null));
            pageable = PageRequest.of(0, 5, Sort.by("reviewDate").descending());
        } else {
           dto.setOwnerReview(null);
            pageable = PageRequest.of(page - 1, 5, Sort.by("reviewDate").descending());
        }
        List<Review> reviewList = reviewRepository.findByProductIdAndUserIdNot(productId, 1L, pageable);
        dto.setOthersReview(reviewList);
        return dto;
    }

    @Transactional
    public void updateReview(Long productId, Long reviewId, NewReview review) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Review dbReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        dbReview.setRating(review.getRating());
        dbReview.setReview(review.getReview());
        dbReview.setUpdatedDate(LocalDateTime.now());
        reviewRepository.save(dbReview);
    }

    @Transactional
    public void saveReview(Long productId, NewReview review) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Optional<Review> optionalReview = reviewRepository.findByProductIdAndUserId(productId, 1L);
        if (optionalReview.isPresent()) {
            throw new InvalidContentException("User already made review for this product");
        }
        Review newReview = Review.builder()
                .rating(review.getRating())
                .review(review.getReview())
                .reviewDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .user(user)
                .product(product)
                .build();
        reviewRepository.save(newReview);
    }

}
