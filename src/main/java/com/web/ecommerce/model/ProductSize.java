package com.web.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonBackReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "size_id",referencedColumnName = "id")
    private Size size;

}
