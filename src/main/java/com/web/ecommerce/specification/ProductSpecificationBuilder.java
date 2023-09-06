package com.web.ecommerce.specification;

import com.web.ecommerce.enumeration.SearchOperation;
import com.web.ecommerce.model.product.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.web.ecommerce.util.Constant.*;

public class ProductSpecificationBuilder {
    private final List<SearchCriteria> params;

    public ProductSpecificationBuilder() {
        params = new ArrayList<>();
    }

    public final ProductSpecificationBuilder withFilter(ProductFilter filter) {
        //TODO: handle case where gender is mistyped
        if (filter.getQ() != null) {
            params.add(new SearchCriteria("name", "eq", filter.getQ().toUpperCase(), false));
        }
        if (filter.getCategory() != null) {
            if (!filter.getCategory().equalsIgnoreCase("ALL")) {
                List<SearchOperation> joinOperations = List.of(SearchOperation.EQUAL);
                List<Object> joinValues = List.of(filter.getCategory().toUpperCase());
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
        if (filter.getRating() != null) {
            params.add(new SearchCriteria("rating", "ge", filter.getRating(), false));
        }
        if (filter.getGender() != null && !filter.getGender().isEmpty()) {
            params.add(new SearchCriteria("gender", "eq", filter.getGender(), true));

        }
        if (filter.getStock() != null) {
            switch (filter.getStock().toUpperCase()) {
                case OUT_OF_STOCK -> {
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
                case LOW_STOCK -> {
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
                case IN_STOCK -> {
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
                case DEFAULT -> {
                    List<SearchOperation> joinOperations = List.of(SearchOperation.GREATER_THAN);
                    List<Object> joinValues = List.of(1);
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
            }
        }
        if (filter.getPublish() != null) {
            String publish = filter.getPublish().toUpperCase();
            if (publish.equals(PUBLISH)) {
                params.add(new SearchCriteria("publish", "eq", true, false));
            } else if (publish.equals(DRAFT)) {
                params.add(new SearchCriteria("publish", "eq", false, false));
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
            result = Specification.where(result).and(new ProductSpecification(params.get(i)));
        }
        return result;
    }

}
