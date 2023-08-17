package com.web.ecommerce.service;

import com.web.ecommerce.dto.user.AuthDTO;
import com.web.ecommerce.dto.user.CreateAddressRequest;
import com.web.ecommerce.model.user.Address;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.AddressRepository;
import com.web.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository){
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public void register(User user) {
        userRepository.save(user);
    }

    public User login(AuthDTO authDTO){
        return userRepository.findByEmail(authDTO.getEmail()).orElseThrow();
    }

    public void addAddress(CreateAddressRequest address){
       Optional<User> user =  userRepository.findById(address.getUserId());
       if(user.isPresent()){
           Address newAddress = Address.builder()
                   .user(user.get())
                   .street(address.getStreet())
                   .city(address.getCity())
                   .state(address.getState())
                   .country(address.getCountry())
                   .zip(address.getZip())
                   .build();
           addressRepository.save(newAddress);
       }else{
           throw new RuntimeException("User does not exist");
       }
    }


}
