package com.web.ecommerce.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "sizes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(columnDefinition = "boolean default true")
    private Boolean publish;
    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime lastModified = LocalDateTime.now();

    @OneToMany(mappedBy = "size")
    @JsonIgnore
    private List<ProductSize> productSizes;


}
