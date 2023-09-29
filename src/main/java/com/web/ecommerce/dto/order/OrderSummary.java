package com.web.ecommerce.dto.order;

import com.web.ecommerce.model.order.OrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderSummary {
    private Long productId;
    private String productName;
    private String productImg;
    private int quantity;
    private double priceAtPurchase;
    private String sizeLabel;

    public static OrderSummary toOrderSummary(OrderDetail orderDetail){
        OrderSummary orderSummary = new OrderSummary();
        orderSummary.setProductName(orderDetail.getProductSize().getProduct().getName());
        orderSummary.setQuantity(orderDetail.getQuantity());
        orderSummary.setPriceAtPurchase(orderDetail.getPriceAtPurchase());
        orderSummary.setSizeLabel(orderDetail.getProductSize().getSize().getName());
        orderSummary.setProductId(orderDetail.getProductSize().getProduct().getId());
        orderSummary.setProductImg(orderDetail.getProductSize().getProduct().getImages().get(0).getImageUrl());
        return orderSummary;
    }

    public static List<OrderSummary> toOrderSummaries(List<OrderDetail> orderDetails){
        return orderDetails.stream()
                .map(OrderSummary::toOrderSummary)
                .collect(Collectors.toList());
    }

}
