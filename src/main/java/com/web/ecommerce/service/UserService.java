package com.web.ecommerce.service;

import com.web.ecommerce.configuration.security.JwtService;
import com.web.ecommerce.dto.user.AuthenticationResponse;
import com.web.ecommerce.dto.user.SignInRequest;
import com.web.ecommerce.dto.user.CreateAddressRequest;
import com.web.ecommerce.dto.user.SignUpRequest;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.model.user.Address;
import com.web.ecommerce.model.user.Role;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.AddressRepository;
import com.web.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository,
                       AddressRepository addressRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthenticationResponse register(SignUpRequest user) {
        Optional<User> dbUser = userRepository.findByEmail(user.getEmail());
        if(dbUser.isPresent()){
            throw new InvalidContentException("User with email " + user.getEmail() + " already existed");
        }
        User newUser = User.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(newUser);
        String jwt = jwtService.generateToken(Map.of("username", user.getEmail()));
        return new AuthenticationResponse(jwt);
    }

    @Transactional
    public AuthenticationResponse login(SignInRequest signInRequest) {
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials."));
        if (bCryptPasswordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            String jwt = jwtService.generateToken(Map.of("username", signInRequest.getEmail()));
            return new AuthenticationResponse(jwt);
        } else {
            throw new BadCredentialsException("Bad credentials.");
        }
    }

    public void addAddress(CreateAddressRequest address) {
        Optional<User> user = userRepository.findById(address.getUserId());
        if (user.isPresent()) {
            Address newAddress = Address.builder()
                    .user(user.get())
                    .street(address.getStreet())
                    .city(address.getCity())
                    .state(address.getState())
                    .country(address.getCountry())
                    .zip(address.getZip())
                    .build();
            addressRepository.save(newAddress);
        } else {
            throw new RuntimeException("User does not exist");
        }
    }


}
