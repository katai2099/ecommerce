package com.web.ecommerce.specification.order;

import com.web.ecommerce.enumeration.SearchOperation;
import com.web.ecommerce.model.order.Order;
import com.web.ecommerce.specification.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification implements Specification<Order> {
    private final SearchCriteria criteria;

    public OrderSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        switch (SearchOperation.getSimpleOperation(criteria.getOperation())) {
            case JOIN -> {
                joinTable(root, criteria.getJoinTable());
                List<Predicate> predicates = constructPredicates(root, builder, criteria);
                if (criteria.isOrPredicate()) {
                    return builder.or(constructOrPredicates(root, builder, criteria).toArray(new Predicate[0]));
                }
                return predicates.get(0);
            }
            default -> {
                return null;
            }
        }
    }

    private List<Predicate> constructPredicates(Root<Order> root,
                                                CriteriaBuilder builder,
                                                SearchCriteria criteria) {
        List<Predicate> predicateList = new ArrayList<>();
        for (int i = 0; i < criteria.getJoinOperations().size(); i++) {
            Predicate predicate = null;
            SearchOperation operation = criteria.getJoinOperations().get(i);
            if (operation == SearchOperation.EQUAL) {
                if (criteria.isOrPredicate()) {
                    return constructOrPredicates(root, builder, criteria);
                }
                predicate = builder.equal(
                        builder.upper(root.get(criteria.getJoinTable()).get(criteria.getKey())),
                        criteria.getJoinValues().get(i).toString()
                );
            }
            predicateList.add(predicate);
        }
        return predicateList;
    }

    private List<Predicate> constructOrPredicates(Root<Order> root,
                                                  CriteriaBuilder builder,
                                                  SearchCriteria criteria) {
        List<Predicate> predicateList = new ArrayList<>();
        if (criteria.getOperation().equals("join")) {
            String[] tokens = criteria.getJoinValues().get(0).toString().split("::");
            for (String token : tokens) {
                Predicate pred = builder.equal(
                        builder.upper(root.get(criteria.getJoinTable()).get(criteria.getKey())),
                        token);
                predicateList.add(pred);
            }
        } else {
            String[] tokens = criteria.getValue().toString().split("::");
            for (String token : tokens) {
                Predicate pred = builder.equal(
                        builder.upper(root.get(criteria.getKey())),
                        token);
                predicateList.add(pred);
            }
        }
        return predicateList;
    }


    private void joinTable(Root<Order> root, String joinTable) {
        root.join(joinTable);
    }

}
