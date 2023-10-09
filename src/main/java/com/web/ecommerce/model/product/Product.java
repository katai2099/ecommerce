package com.web.ecommerce.model.product;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.web.ecommerce.enumeration.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;


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
    @Column(columnDefinition = "boolean default true")
    private boolean publish;
    private double rating;
    @Column(columnDefinition = "boolean default false")
    private boolean isFeatured;

    @Builder.Default()
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference("product-sizes")
    @Builder.Default
    private List<ProductSize> productSizes = new LinkedList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference("product-images")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    @JsonManagedReference(value = "product-reviews")
    private List<Review> reviews = new ArrayList<>();



}
