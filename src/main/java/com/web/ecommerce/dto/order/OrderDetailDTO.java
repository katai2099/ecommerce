package com.web.ecommerce.dto.order;

import com.web.ecommerce.dto.user.UserOrderDetailDTO;
import com.web.ecommerce.model.order.Order;
import com.web.ecommerce.model.order.OrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderDetailDTO {
    private OrderDTO order;
    private UserOrderDetailDTO user;
    private List<OrderSummary> orderSummaries ;
    @Getter
    @Setter
    public static class OrderSummary{
        private Long productId;
        private String productImg;
        private int quantity;
        private double priceAtPurchase;
        private String sizeLabel;
    }

    public static OrderDetailDTO toOrderDetailDTO(Order order){
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrder(OrderDTO.toOrderDTO(order));
        dto.setUser(UserOrderDetailDTO.toUserOrderDetailDTO(order.getUser()));
        dto.setOrderSummaries(toOrderSummaries(order.getOrderDetails()));
        return dto;
    }

    public static OrderSummary toOrderSummary(OrderDetail orderDetail){
        OrderSummary orderSummary = new OrderSummary();
        orderSummary.setQuantity(orderDetail.getQuantity());
        orderSummary.setPriceAtPurchase(orderDetail.getPriceAtPurchase());
        orderSummary.setSizeLabel(orderDetail.getProductSize().getSize().getName());
        orderSummary.setProductId(orderDetail.getProductSize().getProduct().getId());
        //TODO add image after implement S3
//        orderSummary.setProductImg(orderDetail.getProductSize().getProduct().getImages().get(0).getImageUrl());
        orderSummary.setProductImg("");
        return orderSummary;
    }

    public static List<OrderSummary> toOrderSummaries(List<OrderDetail> orderDetails){
        return orderDetails.stream()
                .map(OrderDetailDTO::toOrderSummary)
                .collect(Collectors.toList());
    }

}
