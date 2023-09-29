package com.web.ecommerce.dto.order;

import com.web.ecommerce.dto.user.AddressDTO;
import com.web.ecommerce.model.order.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderDTO {
    private UUID id;
    private String status;
    private LocalDateTime orderDate;
    private double totalPrice;
    private AddressDTO deliveryAddress;
    private AddressDTO billingAddress;
    private List<OrderSummary> orderDetails ;

    public static OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getOrderStatus().getName());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setBillingAddress(AddressDTO.toAddressDTO(order.getBillingAddress()));
        dto.setDeliveryAddress(AddressDTO.toAddressDTO(order.getDeliveryAddress()));
        dto.setOrderDetails(OrderSummary.toOrderSummaries(order.getOrderDetails()));
        return dto;
    }

    public static List<OrderDTO> toOrderDTOS(List<Order> orders) {
        return orders.stream()
                .map(OrderDTO::toOrderDTO)
                .collect(Collectors.toList());
    }

}
