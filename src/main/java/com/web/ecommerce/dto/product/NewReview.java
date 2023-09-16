package com.web.ecommerce.dto.product;

import lombok.Data;

@Data
public class NewReview {
    private String title;
    private String review;
    private double rating;
}
