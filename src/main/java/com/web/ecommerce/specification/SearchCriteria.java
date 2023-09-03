package com.web.ecommerce.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    private String key;
    private String columnName;
    private String operation;
    private Object value;
    private boolean orPredicate;
}
