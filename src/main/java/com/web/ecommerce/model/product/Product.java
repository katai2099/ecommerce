package com.web.ecommerce.model.product;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.web.ecommerce.enumeration.Gender;
import com.web.ecommerce.model.order.OrderDetail;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;
    private String description;
    private double price;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference("product-sizes")
    @Builder.Default
    private Set<ProductSize> productSizes = new HashSet<>();

    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    @JsonManagedReference("product-orders")
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

}