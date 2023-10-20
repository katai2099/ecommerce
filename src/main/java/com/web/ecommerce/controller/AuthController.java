package com.web.ecommerce.controller;

import com.web.ecommerce.dto.user.NewPasswordRequest;
import com.web.ecommerce.dto.user.SignInRequest;
import com.web.ecommerce.dto.user.SignUpRequest;
import com.web.ecommerce.dto.user.UserDTO;
import com.web.ecommerce.service.MailSenderService;
import com.web.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final MailSenderService mailSenderService;

    @Autowired
    public AuthController(UserService userService, MailSenderService mailSenderService) {
        this.userService = userService;
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody SignUpRequest user) {
        UserDTO dto = userService.register(user);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody SignInRequest signInRequest) {
        UserDTO dto = userService.login(signInRequest);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> forgotPassword(@RequestBody SignInRequest request) {
        String resetToken = userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(resetToken);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody NewPasswordRequest newPasswordRequest,
                                                @RequestParam String token) {
        String res = userService.resetPassword(newPasswordRequest,token);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/verify-reset-password-token")
    public ResponseEntity<String> verifyPasswordResetToken(@RequestParam String token){
        String res = userService.verifyPasswordResetToken(token);
        return ResponseEntity.ok(res);
    }


}
