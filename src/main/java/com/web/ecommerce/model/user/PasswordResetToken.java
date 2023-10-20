package com.web.ecommerce.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime expirationTime;


    @OneToOne
    @JoinColumn(columnDefinition = "user_id",referencedColumnName = "id")
    @JsonBackReference("user-reset-password-token")
    private User user;

    public PasswordResetToken(String token,User user){
        super();
        this.token = token;
        this.user = user;
    }

    public PasswordResetToken(String token){
        super();
        this.token = token;
    }


}
