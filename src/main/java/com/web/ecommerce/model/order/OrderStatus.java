package com.web.ecommerce.model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "order_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "orderStatus")
    private List<Order> orders=new ArrayList<>();

    @OneToMany(mappedBy = "status")
    private List<OrderHistory> orderHistories=new ArrayList<>();

    public void addOrder(Order order){
        this.orders.add(order);
    }

}
