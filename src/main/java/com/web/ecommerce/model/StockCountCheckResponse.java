package com.web.ecommerce.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockCountCheckResponse {
    private Long cartItemId;
    private int stockCount;
}
