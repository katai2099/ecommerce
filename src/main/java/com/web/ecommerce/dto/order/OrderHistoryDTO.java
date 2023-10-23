package com.web.ecommerce.dto.order;

import com.web.ecommerce.model.order.OrderHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderHistoryDTO {
    private Long id;
    private String actionTime;
    private String status;

    public static OrderHistoryDTO toOrderHistoryDTO(OrderHistory orderHistory) {
        OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO();
        orderHistoryDTO.setId(orderHistory.getId());
        orderHistoryDTO.setActionTime(orderHistory.getActionTime().toString());
        orderHistoryDTO.setStatus(orderHistory.getStatus().getName());
        return orderHistoryDTO;
    }

    public static List<OrderHistoryDTO> toOrderHistoryDTOS(List<OrderHistory> orderHistories) {
        return orderHistories
                .stream()
                .map(OrderHistoryDTO::toOrderHistoryDTO)
                .toList();
    }

}
