package com.web.ecommerce.model.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.web.ecommerce.model.product.ProductSize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "order_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private double priceAtPurchase;

    @ManyToOne()
    @JoinColumn(name = "order_id",referencedColumnName = "id")
    @JsonBackReference("order-details")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_size_id",referencedColumnName = "id")
    @JsonBackReference("product-size-orders")
    private ProductSize productSize;

}
