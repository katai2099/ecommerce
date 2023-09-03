package com.web.ecommerce.specification;

import lombok.Data;

@Data
public class ProductFilter {
    private String query;//1
    private String sort;//1
    private String category;//1
    private String stock;//1
    private String publish;//1
    private String gender;//1
    private double pmin= 0;//1
    private double pmax = 10000;//1
    private String rating;
    private int page = 1;//1
}
