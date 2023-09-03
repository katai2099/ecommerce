package com.web.ecommerce.controller;

import com.web.ecommerce.dto.user.SignInRequest;
import com.web.ecommerce.dto.user.SignUpRequest;
import com.web.ecommerce.dto.user.UserDTO;
import com.web.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
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
}
