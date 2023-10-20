package com.web.ecommerce.model.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.web.ecommerce.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceId;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonBackReference("user-carts")
    private User user;


    @OneToMany(mappedBy = "cart",fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference(value = "cart-items")
    private Set<CartItem> cartItems = new HashSet<>();
    public void addToCart(CartItem cartItem){
        cartItem.setCart(this);
        this.cartItems.add(cartItem);
    }
    public void removeCartItem(CartItem cartItem){
        cartItem.setCart(null);
        this.cartItems.remove(cartItem);
    }

    public void clearCart(){
        this.cartItems.clear();
    }



}
