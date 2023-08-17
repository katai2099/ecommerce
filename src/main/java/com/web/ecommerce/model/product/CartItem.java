package com.web.ecommerce.model.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity(name = "cart_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "cart_id",referencedColumnName = "id")
    @JsonBackReference(value = "cart-items")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_size_id",referencedColumnName = "id")
    @JsonBackReference(value = "product-size-carts")
    private ProductSize productSize;

    public void removeFromCart(){
        cart.removeCartItem(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
