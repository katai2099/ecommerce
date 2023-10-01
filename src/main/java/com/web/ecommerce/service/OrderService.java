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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional
    public PaginationResponse<OrderDTO> getOrders(int page) {
        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by("orderDate").descending());
        Page<Order> orderLists = orderRepository.findAll(pageable);
        List<OrderDTO> orders = OrderDTO.toOrderDTOS(orderLists.stream().toList());
        return PaginationResponse.<OrderDTO>builder()
                .currentPage(page)
                .totalPage(orderLists.getTotalPages())
                .totalItem(orderLists.getNumberOfElements())
                .data(orders)
                .build();
    }

    public List<OrderDTO> getUserOrders(int page){
        Long userId = getUserIdFromSecurityContext();
        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by("orderDate").descending());
        List<Order> orders = orderRepository.findAllByUserId(userId,pageable);
        return OrderDTO.toOrderDTOS(orders);
    }

    @Transactional
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
