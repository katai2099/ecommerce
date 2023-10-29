package com.web.ecommerce.controller;

import com.web.ecommerce.dto.user.NewPasswordRequest;
import com.web.ecommerce.dto.user.SignInData;
import com.web.ecommerce.dto.user.UserDTO;
import com.web.ecommerce.model.SignInRequest;
import com.web.ecommerce.model.SignUpRequest;
import com.web.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody SignUpRequest request) {
        UserDTO dto = userService.register(request.getSignUpData(), request.getDeviceId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody SignInRequest request) {
        UserDTO dto = userService.login(request.getSignInData(), request.getDeviceId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> forgotPassword(@RequestBody SignInData request) {
        String resetToken = userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(resetToken);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody NewPasswordRequest newPasswordRequest,
                                                @RequestParam String token) {
        String res = userService.resetPassword(newPasswordRequest, token);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/verify-reset-password-token")
    public ResponseEntity<String> verifyPasswordResetToken(@RequestParam String token) {
        String res = userService.verifyPasswordResetToken(token);
        return ResponseEntity.ok(res);
    }


}
