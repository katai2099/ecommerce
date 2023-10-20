package com.web.ecommerce.service;

import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.user.PasswordResetToken;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.web.ecommerce.util.Constant.TOKEN_EXPIRATION_TIME;
import static com.web.ecommerce.util.Util.getTokenExpirationTime;

@Service
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public void createPasswordResetTokenForUser(User user, String passwordToken){
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUserId(user.getId());
        if(passwordResetToken==null){
            passwordResetToken = new PasswordResetToken(passwordToken,user);
        }
        passwordResetToken.setToken(passwordToken);
        passwordResetToken.setExpirationTime(getTokenExpirationTime(TOKEN_EXPIRATION_TIME));
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public String validatePasswordResetToken(String passwordResetToken){
        PasswordResetToken token = passwordResetTokenRepository.findByToken(passwordResetToken);
        if(token==null){
            throw new ResourceNotFoundException("Invalid password reset token");
        }
        if(token.getExpirationTime().isBefore(LocalDateTime.now())){
            throw new InvalidContentException("Link already expired, resend link");
        }
        return "valid";
    }

}
