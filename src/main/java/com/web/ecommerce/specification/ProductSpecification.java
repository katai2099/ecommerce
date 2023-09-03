package com.web.ecommerce.specification;

import com.web.ecommerce.enumeration.Gender;
import com.web.ecommerce.enumeration.SearchOperation;
import com.web.ecommerce.model.product.Category;
import com.web.ecommerce.model.product.Product;
import com.web.ecommerce.model.product.ProductSize;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification implements Specification<Product> {
    private final SearchCriteria criteria;

    public ProductSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String strToSearch = criteria.getValue().toString().toLowerCase();
        switch (SearchOperation.getSimpleOperation(criteria.getOperation())) {
            case EQUAL -> {
                if (criteria.getKey().equals("category")) {
                    return builder.equal(
                            builder.upper(categoryJoin(root).get(criteria.getColumnName())), criteria.getValue());
                }
                if (criteria.getKey().equals("stock")) {
                    query.groupBy(root.get("id"));
                    query.having(
                            builder.equal(
                                    builder.sum(productSizeJoin(root).get(criteria.getColumnName())), criteria.getValue()));
                    return query.getRestriction();
                }
                if (root.get(criteria.getKey()).getJavaType() == String.class) {
                    return builder.like(
                            builder.upper(root.get(criteria.getKey())), "%" + strToSearch + "%");
                } else if (root.get(criteria.getKey()).getJavaType().isEnum()) {
                    return builder.equal(
                            root.get(criteria.getKey()), Gender.valueOf(criteria.getValue().toString()));
                } else {
                    return builder.equal(
                            root.get(criteria.getKey()), criteria.getValue());
                }
            }
            case GREATER_THAN -> {
                if (criteria.getKey().equals("stock")) {
                    query.groupBy(root.get("id"));
                    query.having(
                            builder.greaterThan(
                                    builder.sum(productSizeJoin(root).get(criteria.getColumnName())),
                                    Double.valueOf(criteria.getValue().toString())));
                    return query.getRestriction();
//                    query.groupBy(root.get("id"));

//                    Predicate havingClause = builder.greaterThan(
//                            builder.sum(
//                                    productSizeJoin(root).get(criteria.getColumnName())
//                            ),Double.valueOf(criteria.getValue().toString())
//                    );
//                    return havingClause;
//                    return null;
                } else {
                    return builder.greaterThan(
                            root.get(criteria.getKey()), criteria.getValue().toString()
                    );
                }
            }
            case GREATER_THAN_EQUAL -> {
                return builder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            }
            case LESS_THAN_EQUAL -> {
                if (criteria.getKey().equals("stock")) {
                    query.groupBy(root.get("id"));
                    productSizeJoin(root);
                    query.having(
                            builder.and(
                                    builder.greaterThan(
                                            builder.sum(root.get("productSizes").get(criteria.getColumnName())),
                                            0
                                    ),
                                    builder.lessThanOrEqualTo(
                                            builder.sum(root.get("productSizes").get(criteria.getColumnName())),
                                            Double.valueOf(criteria.getValue().toString())
                                    )
                            )
                    );
                    return query.getRestriction();
                } else {
                    return builder.lessThanOrEqualTo(
                            root.get(criteria.getKey()), criteria.getValue().toString());
                }
            }
            default -> {
                return null;
            }
        }
    }

    private Join<Product, Category> categoryJoin(Root<Product> root) {
        return root.join("category");
    }

    private Join<Product, ProductSize> productSizeJoin(Root<Product> root) {
        return root.join("productSizes");
    }

}
