package com.web.ecommerce.dto.order;

import com.web.ecommerce.model.order.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderDetailDTO {
    private OrderDTO order;
    private UserOrderDetailDTO user;
    private List<OrderHistoryDTO> orderHistories;

    public static OrderDetailDTO toOrderDetailDTO(Order order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrder(OrderDTO.toOrderDTO(order));
        dto.setUser(UserOrderDetailDTO.toUserOrderDetailDTO(order.getUser()));
        dto.setOrderHistories(OrderHistoryDTO.toOrderHistoryDTOS(order.getOrderHistories()));
        return dto;
    }

    public static List<OrderDetailDTO> toOrderDetailDTOs(List<Order> orders) {
        return orders.stream()
                .map(OrderDetailDTO::toOrderDetailDTO)
                .collect(Collectors.toList());
    }

}
