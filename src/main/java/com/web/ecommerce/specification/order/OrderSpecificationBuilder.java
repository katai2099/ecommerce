package com.web.ecommerce.specification.order;

import com.web.ecommerce.enumeration.SearchOperation;
import com.web.ecommerce.model.order.Order;
import com.web.ecommerce.specification.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecificationBuilder {
    private final List<SearchCriteria> params;

    public OrderSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final OrderSpecificationBuilder withFilter(OrderFilter filter) {
        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            List<SearchOperation> joinOperations = List.of(SearchOperation.EQUAL);
            List<Object> joinValues = List.of(filter.getStatus().toUpperCase());
            SearchCriteria statusSearchCriteria = SearchCriteria.builder()
                    .key("name")
                    .operation("join")
                    .value(0)
                    .orPredicate(true)
                    .isAggregate(false)
                    .joinTable("orderStatus")
                    .joinOperations(joinOperations)
                    .joinValues(joinValues)
                    .build();
            params.add(statusSearchCriteria);
        }
        return this;
    }

    public Specification<Order> build() {
        if (params.isEmpty()) {
            return null;
        }
        Specification<Order> result = new OrderSpecification(params.get(0));
        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new OrderSpecification(params.get(i)));
        }
        return result;
    }
}
