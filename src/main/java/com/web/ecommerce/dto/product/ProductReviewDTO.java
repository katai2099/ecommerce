package com.web.ecommerce.dto.product;

import com.web.ecommerce.dto.PaginationResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductReviewDTO {
    private ReviewDTO ownerReview;
    private PaginationResponse<ReviewDTO> othersReview;
}
