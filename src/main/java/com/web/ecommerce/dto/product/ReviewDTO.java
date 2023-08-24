package com.web.ecommerce.dto.product;

import com.web.ecommerce.model.product.Review;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReviewDTO {
    private Review ownerReview;
    private List<Review> othersReview;
}
