package com.web.ecommerce.controller;

import com.web.ecommerce.dto.user.CreateAddressRequest;
import com.web.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController (UserService userService){
        this.userService = userService;
    }

    @PostMapping("/address")
    public ResponseEntity<String> addAddress(@RequestBody CreateAddressRequest address){
        userService.addAddress(address);
        return new ResponseEntity<>("Successfully Created", HttpStatus.CREATED);
    }

}