package com.web.ecommerce.specification;

import com.web.ecommerce.enumeration.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
    private boolean orPredicate;

    //extra data for join operations;
    private boolean isAggregate;
    //having/where clause
    private List<SearchOperation> joinOperations;
    //join table name
    private String joinTable;
    //having/where clause value
    private List<Object> joinValues;

    public SearchCriteria(String key, String operation, Object value, boolean orPredicate){
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.orPredicate = orPredicate;
        joinOperations = new ArrayList<>();
        isAggregate = false;
        joinTable = "";
        joinValues= new ArrayList<>();
    }
}
