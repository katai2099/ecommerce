package com.web.ecommerce.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.web.ecommerce.model.order.Order;
import com.web.ecommerce.model.product.Cart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference(value = "user-address")
    private Address address;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference(value = "user-orders")
    private List<Order> orders = new ArrayList<>();

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonManagedReference(value = "user-carts")
    private Cart cart;

    public void addOrder(Order order){
        orders.add(order);
    }

}
