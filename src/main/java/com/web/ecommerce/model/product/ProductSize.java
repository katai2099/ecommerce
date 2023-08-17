package com.web.ecommerce.model.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.web.ecommerce.model.order.OrderDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "product_size")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int stockCount;

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    @JsonBackReference("product-sizes")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "size_id",referencedColumnName = "id")
    private Size size;

    @OneToMany(mappedBy = "productSize",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonManagedReference(value = "product-size-carts")
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "productSize",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSize that = (ProductSize) o;
        return Objects.equals(id, that.id) && Objects.equals(size.getName(), that.size.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, size.getName());
    }
}
