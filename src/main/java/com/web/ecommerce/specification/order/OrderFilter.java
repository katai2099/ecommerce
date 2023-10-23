package com.web.ecommerce.specification.order;

import lombok.Data;

@Data
public class OrderFilter {
    private int itemperpage = 100;
    private int page = 1;
    private String status;
}
