package com.web.ecommerce.specification;

import lombok.Data;

@Data
public class ProductFilter {
    private String query;//1
    private String sort;
    private String category;//1
    private String stock;
    private String publish;
    private String gender;//1
    private double pmin= 0;//1
    private double pmax = 10000;//1
    private String rating;
    private int page = 1;//1
}
