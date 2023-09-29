package com.web.ecommerce.model.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.web.ecommerce.model.user.Address;
import com.web.ecommerce.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity()
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = UUID.randomUUID();
    private LocalDateTime orderDate;
    private double TotalPrice;
    private String stripePaymentIntentId;

    @OneToOne
    @JoinColumn(name = "delivery_address_id",referencedColumnName = "id")
    private Address deliveryAddress;
    @OneToOne
    @JoinColumn(name = "billing_address_id",referencedColumnName = "id")
    private Address billingAddress;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonBackReference(value = "user-orders")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_status_id",referencedColumnName = "id")
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference(value = "order-details")
    List<OrderDetail> orderDetails = new ArrayList<>();

    public void addOrderDetail(OrderDetail orderDetail){
        this.orderDetails.add(orderDetail);
    }

}
