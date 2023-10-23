package com.web.ecommerce.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderAnalytic {
    private Long totalSales;
    private Long todaySales;
    private String oldestOrderDate;
    private Long totalOrders;
    private Long todayOrders;
}
