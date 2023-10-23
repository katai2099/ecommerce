package com.web.ecommerce.service;

import com.web.ecommerce.dto.PaginationResponse;
import com.web.ecommerce.dto.order.OrderDTO;
import com.web.ecommerce.dto.order.OrderDetailDTO;
import com.web.ecommerce.enumeration.OrderStatusEnum;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.order.Order;
import com.web.ecommerce.model.order.OrderStatus;
import com.web.ecommerce.repository.OrderRepository;
import com.web.ecommerce.repository.OrderStatusRepository;
import com.web.ecommerce.specification.order.OrderFilter;
import com.web.ecommerce.specification.order.OrderSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.web.ecommerce.util.Util.getUserIdFromSecurityContext;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderStatusRepository orderStatusRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    public PaginationResponse<OrderDetailDTO> getOrders(OrderFilter filter) {
        Pageable pageable = PageRequest.of(filter.getPage() - 1, filter.getItemperpage(), Sort.by("orderDate").descending());
        OrderSpecificationBuilder builder = new OrderSpecificationBuilder();
        builder.withFilter(filter);
        Specification<Order> spec = builder.build();
        Page<Order> orderLists = orderRepository.findAll(spec, pageable);
        List<OrderDetailDTO> orders = OrderDetailDTO.toOrderDetailDTOs(orderLists.stream().toList());
        return PaginationResponse.<OrderDetailDTO>builder()
                .currentPage(filter.getPage())
                .totalPage(orderLists.getTotalPages())
                .totalItem(orderLists.getNumberOfElements())
                .data(orders)
                .build();
    }

    public PaginationResponse<OrderDTO> getUserOrders(int page) {
        Long userId = getUserIdFromSecurityContext();
        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by("orderDate").descending());

        Page<Order> orderPage = orderRepository.findAllByUserId(userId, pageable);
        PaginationResponse<OrderDTO> orderDTOPaginationResponse = PaginationResponse.<OrderDTO>builder()
                .currentPage(page)
                .totalPage(orderPage.getTotalPages())
                .totalItem(orderPage.getTotalElements())
                .data(OrderDTO.toOrderDTOS(orderPage.toList()))
                .build();
        return orderDTOPaginationResponse;
    }

    public OrderDetailDTO getOrder(UUID orderId) {
        Order order = orderRepository.findByOrderUuid(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with Id " + orderId + " not found"));
        return OrderDetailDTO.toOrderDetailDTO(order);
    }

    @Transactional
    public String updateOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidContentException("Order with " + orderId + " does not exist"));
        OrderStatus orderStatus = order.getOrderStatus();
        OrderStatusEnum nextOrderStatusEnum = OrderStatusEnum.getNextOrderStatus(OrderStatusEnum.valueOf(orderStatus.getName()));
        OrderStatus nextOrderStatus = orderStatusRepository.findByName(nextOrderStatusEnum.toString())
                .orElseThrow(() -> new InvalidContentException("Status does not exist"));
        order.setOrderStatus(nextOrderStatus);
        orderRepository.save(order);
        return nextOrderStatusEnum.toString();
    }

}
