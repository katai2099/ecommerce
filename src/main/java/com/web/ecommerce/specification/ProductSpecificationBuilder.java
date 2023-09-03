package com.web.ecommerce.specification;

import com.web.ecommerce.enumeration.SearchOperation;
import com.web.ecommerce.model.product.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductSpecificationBuilder {
    private final List<SearchCriteria> params;

    public ProductSpecificationBuilder() {
        params = new ArrayList<>();
    }

    public final ProductSpecificationBuilder withFilter(ProductFilter filter) {
        //TODO: handle case where gender is mistyped
        if (filter.getCategory() != null) {
            String[] tokens = filter.getCategory().split("::");
            for (String category : tokens) {
                List<SearchOperation> joinOperations = List.of(SearchOperation.EQUAL);
                List<Object> joinValues = List.of(category.toUpperCase());
                SearchCriteria categorySearchCriteria = SearchCriteria.builder()
                        .key("name")
                        .operation("join")
                        .value(0)
                        .orPredicate(true)
                        .isAggregate(false)
                        .joinTable("category")
                        .joinOperations(joinOperations)
                        .joinValues(joinValues)
                        .build();
                params.add(categorySearchCriteria);
            }
        }
        if (filter.getGender() != null) {
            params.add(new SearchCriteria("gender", "eq", filter.getGender().toUpperCase(), false));
        }
        if (filter.getQuery() != null) {
            params.add(new SearchCriteria("name", "eq", filter.getQuery().toUpperCase(), false));
        }
        if (filter.getStock() != null) {
            switch (filter.getStock().toUpperCase()) {
                case "OUTOFSTOCK" -> {
                    List<SearchOperation> joinOperations = List.of(SearchOperation.EQUAL);
                    List<Object> joinValues = List.of(0);
                    SearchCriteria lowStockSearchCriteria = SearchCriteria.builder()
                            .key("stockCount")
                            .operation("join")
                            .value(0)
                            .orPredicate(false)
                            .isAggregate(true)
                            .joinTable("productSizes")
                            .joinOperations(joinOperations)
                            .joinValues(joinValues)
                            .build();
                    params.add(lowStockSearchCriteria);
                }
                case "LOWSTOCK" -> {
                    List<SearchOperation> joinOperations = Arrays.asList(SearchOperation.LESS_THAN_EQUAL, SearchOperation.GREATER_THAN);
                    List<Object> joinValues = Arrays.asList(20, 0);
                    SearchCriteria lowStockSearchCriteria = SearchCriteria.builder()
                            .key("stockCount")
                            .operation("join")
                            .value(0)
                            .orPredicate(false)
                            .isAggregate(true)
                            .joinTable("productSizes")
                            .joinOperations(joinOperations)
                            .joinValues(joinValues)
                            .build();
                    params.add(lowStockSearchCriteria);
                }
                case "INSTOCK" -> {
                    List<SearchOperation> joinOperations = List.of(SearchOperation.GREATER_THAN);
                    List<Object> joinValues = List.of(20);
                    SearchCriteria inStockSearchCriteria = SearchCriteria.builder()
                            .key("stockCount")
                            .operation("join")
                            .value(0)
                            .orPredicate(false)
                            .isAggregate(true)
                            .joinTable("productSizes")
                            .joinOperations(joinOperations)
                            .joinValues(joinValues)
                            .build();
                    params.add(inStockSearchCriteria);
                }
            }
        }
        params.add(new SearchCriteria(
                "price", SearchOperation.GREATER_THAN_EQUAL.toString(), filter.getPmin(), false));
        params.add(new SearchCriteria(
                "price", SearchOperation.LESS_THAN_EQUAL.toString(), filter.getPmax(), false));
        return this;
    }

    public Specification<Product> build() {
        if (params.isEmpty()) {
            return null;
        }
        Specification<Product> result = new ProductSpecification(params.get(0));
        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new ProductSpecification(params.get(i)))
                    : Specification.where(result).and(new ProductSpecification(params.get(i)));
        }
        return result;
    }

}
