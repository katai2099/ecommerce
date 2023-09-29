package com.web.ecommerce.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "address")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String street;
    private String houseNumber;
    private String city;
    private String country;
    private String zipCode;
    @Column(columnDefinition = "boolean default false")
    private boolean isDefault;


    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonBackReference(value = "user-address")
    private User user;

}
