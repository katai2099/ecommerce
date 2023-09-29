package com.web.ecommerce.dto.order;

import com.web.ecommerce.model.order.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDTO {
    private OrderDTO order;
    private UserOrderDetailDTO user;


    public static OrderDetailDTO toOrderDetailDTO(Order order){
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrder(OrderDTO.toOrderDTO(order));
        dto.setUser(UserOrderDetailDTO.toUserOrderDetailDTO(order.getUser()));
        return dto;
    }

}
