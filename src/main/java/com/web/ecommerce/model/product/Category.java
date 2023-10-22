package com.web.ecommerce.model.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(columnDefinition = "boolean default false")
    private Boolean isTop;
    @Column(columnDefinition = "boolean default true")
    private Boolean publish;
    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime lastModified = LocalDateTime.now();

    private String categoryImage;

}
