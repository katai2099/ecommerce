package com.web.ecommerce.specification;

import com.web.ecommerce.model.product.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
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
            for(String category: tokens){
                params.add(new SearchCriteria("category", "name", "eq", category.toUpperCase(), true));
            }
        }
        if (filter.getGender() != null) {
            params.add(new SearchCriteria("gender", "gender", "eq", filter.getGender().toUpperCase(), false));
        }
        if (filter.getQuery() != null) {
            params.add(new SearchCriteria("name", "name", "eq", filter.getQuery().toUpperCase(), false));
        }
        if(filter.getStock()!=null){
            switch (filter.getStock().toUpperCase()) {
                case "OUTOFSTOCK" -> {
                    params.add(new SearchCriteria("stock", "stockCount", "eq", 0, false));
                }
                case "LOWSTOCK" -> {
                    params.add(new SearchCriteria("stock", "stockCount", "le", 20, false));
//                    params.add(new SearchCriteria("stock", "stockCount", "gt", 0, false));
                }
                case "INSTOCK" -> {
                    params.add(new SearchCriteria("stock", "stockCount", "gt", 20, false));
                }
            }
        }
//        params.add(new SearchCriteria(
//                "price", "price", SearchOperation.GREATER_THAN_EQUAL.toString(), filter.getPmin(), false));
//        params.add(new SearchCriteria(
//                "price", "price", SearchOperation.LESS_THAN_EQUAL.toString(), filter.getPmax(), false));


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
