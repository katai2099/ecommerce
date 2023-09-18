package com.web.ecommerce.controller;

import com.web.ecommerce.dto.user.AddressDTO;
import com.web.ecommerce.dto.user.NewPasswordRequest;
import com.web.ecommerce.dto.user.UpdateDetailsRequest;
import com.web.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController (UserService userService){
        this.userService = userService;
    }

    @PutMapping("/details")
    public ResponseEntity<Long> updateDetails(@RequestBody UpdateDetailsRequest detailsRequest){
        Long userId = userService.updateDetails(detailsRequest);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody NewPasswordRequest passwordRequest){
        userService.updatePassword(passwordRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/address")
    public ResponseEntity<List<AddressDTO>> getAddresses(){
        List<AddressDTO> addresses = userService.getAddresses();
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/address")
    public ResponseEntity<Long> addAddress(@RequestBody AddressDTO address){
        Long addressId = userService.addAddress(address);
        return ResponseEntity.ok(addressId);
    }

    @PutMapping("/address/{id}")
    public ResponseEntity<Long> updateAddress(@PathVariable Long id,
                                              @RequestBody AddressDTO addressDTO){
        Long addressId = userService.updateAddress(addressDTO,id);
        return ResponseEntity.ok(addressId);
    }

    @GetMapping("/address/set-default/{id}")
    public ResponseEntity<Long> setDefaultAddress(@PathVariable Long id){
        Long addressId = userService.setDefaultAddress(id);
        return ResponseEntity.ok(addressId);
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id){
        userService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

}
