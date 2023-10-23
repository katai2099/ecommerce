package com.web.ecommerce.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MonthlySaleData {
    private Double totalSales;
    private Integer month;
    private Integer year;
    public MonthlySaleData(Double totalSales, Integer month, Integer year) {
        this.totalSales = totalSales;
        this.month = month;
        this.year = year;
    }
}
