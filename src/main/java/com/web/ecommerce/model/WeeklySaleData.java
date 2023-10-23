package com.web.ecommerce.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WeeklySaleData {
    private Double totalSales;
    private Integer dayOfWeek;

    public WeeklySaleData(Double totalSales, Integer dayOfWeek) {
        this.totalSales = totalSales;
        this.dayOfWeek = dayOfWeek;
    }

}
