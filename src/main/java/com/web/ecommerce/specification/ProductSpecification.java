package com.web.ecommerce.specification;

import com.web.ecommerce.enumeration.Gender;
import com.web.ecommerce.enumeration.SearchOperation;
import com.web.ecommerce.model.product.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification implements Specification<Product> {
    private final SearchCriteria criteria;

    public ProductSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String strToSearch = criteria.getValue().toString().toUpperCase();
        switch (SearchOperation.getSimpleOperation(criteria.getOperation())) {
            case EQUAL -> {
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
                return builder.greaterThan(
                        root.get(criteria.getKey()), criteria.getValue().toString()
                );
            }
            case GREATER_THAN_EQUAL -> {
                return builder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            }
            case LESS_THAN_EQUAL -> {
                return builder.lessThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            }
            case JOIN -> {
                joinTable(root, criteria.getJoinTable());
                List<Predicate> predicates = constructPredicates(root, builder, criteria);
                if (criteria.isAggregate()) {
                    query.groupBy(root.get("id"));
                    query.having(builder.and(predicates.toArray(new Predicate[0])));
                    return query.getRestriction();
                }
                return predicates.get(0);
            }
            default -> {
                return null;
            }
        }
    }

    private List<Predicate> constructPredicates(Root<Product> root,
                                                CriteriaBuilder builder,
                                                SearchCriteria criteria) {
        List<Predicate> predicateList = new ArrayList<>();
        for (int i = 0; i < criteria.getJoinOperations().size(); i++) {
            Predicate predicate = null;
            SearchOperation operation = criteria.getJoinOperations().get(i);
            if (criteria.isAggregate()) {
                switch (operation) {
                    case GREATER_THAN -> predicate = builder.greaterThan(
                            builder.sum(root.get(criteria.getJoinTable()).get(criteria.getKey())),
                            Double.valueOf(criteria.getJoinValues().get(i).toString())
                    );
                    case LESS_THAN_EQUAL -> predicate = builder.lessThanOrEqualTo(
                            builder.sum(root.get(criteria.getJoinTable()).get(criteria.getKey())),
                            Double.valueOf(criteria.getJoinValues().get(i).toString())
                    );
                    case EQUAL -> predicate = builder.equal(
                            builder.sum(root.get(criteria.getJoinTable()).get(criteria.getKey())),
                            Double.valueOf(criteria.getJoinValues().get(i).toString())
                    );
                }
            } else {
                if (operation == SearchOperation.EQUAL) {
                    predicate = builder.equal(
                            builder.upper(root.get(criteria.getJoinTable()).get(criteria.getKey())),
                            criteria.getJoinValues().get(i).toString()
                    );
                }
            }
            predicateList.add(predicate);

        }
        return predicateList;
    }

    private void joinTable(Root<Product> root, String joinTable) {
        root.join(joinTable);
    }

}
