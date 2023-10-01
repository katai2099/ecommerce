package com.web.ecommerce.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerId;

    @OneToOne()
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @JsonBackReference(value = "user-stripe-customer")
    private User user;

}
