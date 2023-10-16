package com.web.ecommerce.service;

import com.web.ecommerce.dto.PaginationResponse;
import com.web.ecommerce.dto.product.NewReview;
import com.web.ecommerce.dto.product.ReviewDTO;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.product.Product;
import com.web.ecommerce.model.product.Review;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.ProductRepository;
import com.web.ecommerce.repository.ReviewRepository;
import com.web.ecommerce.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.web.ecommerce.util.Util.getUserIdFromSecurityContext;

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
    public ReviewDTO getUserReview(Long productId) {
        Long userId = getUserIdFromSecurityContext();
        if (userId == -1) {
            throw new ResourceNotFoundException("No user review");
        }
        Review ownerReview = reviewRepository.findByProductIdAndUserId(productId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("No user review"));
        return ReviewDTO.toReviewDTO(ownerReview);
    }

    @Transactional
    public PaginationResponse<ReviewDTO> getReviews(Long productId, Integer page) {
        Long userId = getUserIdFromSecurityContext();
        Pageable pageable = PageRequest.of(page - 1, 5, Sort.by("reviewDate").descending());
        Page<Review> reviewList = reviewRepository.findByProductIdAndUserIdNot(productId, userId, pageable);
        PaginationResponse<ReviewDTO> otherReviews = PaginationResponse.<ReviewDTO>builder()
                .currentPage(page)
                .totalPage(reviewList.getTotalPages())
                .totalItem(reviewList.getTotalElements())
                .data(ReviewDTO.toReviewDTOs(reviewList.toList()))
                .build();
        return otherReviews;
    }

    @Transactional
    public Long saveReview(Long productId, NewReview review) {
        Long userId = getUserIdFromSecurityContext();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Optional<Review> optionalReview = reviewRepository.findByProductIdAndUserId(productId, userId);
        Review reviewToSave;
        if (optionalReview.isPresent()) {
            Review existingReview = optionalReview.get();
            existingReview.setRating(review.getRating());
            existingReview.setTitle(review.getTitle());
            existingReview.setReview(review.getReview());
            existingReview.setUpdatedDate(LocalDateTime.now());
            reviewToSave = existingReview;
        } else {
            reviewToSave = Review.builder()
                    .rating(review.getRating())
                    .title(review.getTitle())
                    .review(review.getReview())
                    .reviewDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .user(user)
                    .product(product)
                    .build();
        }
        Review updatedReview = reviewRepository.save(reviewToSave);
        List<Review> productReviews = product.getReviews();
        double totalReviews = productReviews.size();
        double totalRating = productReviews.stream()
                .mapToDouble(Review::getRating)
                .sum();
        double averageRating = totalReviews > 0 ? totalRating / totalReviews : 0.0;
        product.setRating(averageRating);
        productRepository.save(product);
        return updatedReview.getId();
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
