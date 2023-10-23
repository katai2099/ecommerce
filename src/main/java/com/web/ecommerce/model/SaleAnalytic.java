package com.web.ecommerce.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaleAnalytic {
    List<WeeklySaleData> weeklySaleData;
    List<MonthlySaleData> monthlySaleData;
}
