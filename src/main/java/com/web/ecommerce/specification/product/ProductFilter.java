package com.web.ecommerce.specification.product;

import lombok.Data;

import static com.web.ecommerce.util.Constant.PUBLISH;

@Data
public class ProductFilter {
    private String q;//1
    private String sort;//1
    private String category;//1
    private String stock;//1
    private String publish = PUBLISH;//1
    private String gender;//1
    private double pmin = 0;//1
    private double pmax = 10000;//1
    private Integer rating = 0;
    private int page = 1;//1
    private int itemperpage = 20;
}
