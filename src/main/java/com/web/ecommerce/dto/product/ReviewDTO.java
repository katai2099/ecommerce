package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Review;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ReviewDTO {
    private Long id;
    private double rating;
    private String title;
    private String review;
    private LocalDateTime reviewDate;
    private LocalDateTime updatedDate;
    private String reviewer;

    public static ReviewDTO toReviewDTO(Review review){
        return ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .title(review.getTitle())
                .review(review.getReview())
                .reviewDate(review.getReviewDate())
                .updatedDate(review.getUpdatedDate())
                .reviewer(review.getUser().getFirstname() + " " + review.getUser().getLastname())
                .build();
    }

    public static List<ReviewDTO> toReviewDTOs(List<Review> reviews){
        return reviews.stream()
                .map(ReviewDTO::toReviewDTO)
                .collect(Collectors.toList());
    }

}
