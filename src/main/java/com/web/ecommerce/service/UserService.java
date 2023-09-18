package com.web.ecommerce.service;

import com.web.ecommerce.configuration.security.JwtService;
import com.web.ecommerce.dto.user.AddressDTO;
import com.web.ecommerce.dto.user.SignInRequest;
import com.web.ecommerce.dto.user.SignUpRequest;
import com.web.ecommerce.dto.user.UserDTO;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.web.ecommerce.util.Util.getUserIdFromSecurityContext;


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
    public UserDTO register(SignUpRequest user) {
        Optional<User> dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser.isPresent()) {
            throw new InvalidContentException("User with email " + user.getEmail() + " already existed");
        }
        User newUser = User.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(newUser);
        String jwt = jwtService.generateToken(Map.of("username", user.getEmail()));
        UserDTO userDTO = UserDTO.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .role(newUser.getRole().name())
                .token(jwt)
                .build();
        return userDTO;
    }

    @Transactional
    public UserDTO login(SignInRequest signInRequest) {
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials."));
        if (bCryptPasswordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            String jwt = jwtService.generateToken(Map.of("username", signInRequest.getEmail()));
            UserDTO userDTO = UserDTO.builder()
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .token(jwt)
                    .build();
            return userDTO;
        } else {
            throw new BadCredentialsException("Bad credentials.");
        }
    }

    @Transactional
    public Long addAddress(AddressDTO address) {
        Long userId = getUserIdFromSecurityContext();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(address.getIsDefault()!=null && address.getIsDefault()){
            unsetDefaultAddress(userId,-1L);
        }
        Address newAddress = Address.builder()
                .user(user)
                .firstname(address.getFirstname())
                .lastname(address.getLastname())
                .phoneNumber(address.getPhoneNumber())
                .street(address.getStreet())
                .houseNumber(address.getHouseNumber())
                .city(address.getCity())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .isDefault(address.getIsDefault())
                .build();
        Address savedAddress = addressRepository.save(newAddress);
        return savedAddress.getId();
    }

    @Transactional
    public Long updateAddress(AddressDTO addressDTO,Long addressId) {
        Long userId = getUserIdFromSecurityContext();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " not found"));
        if(addressDTO.getIsDefault()!=null && addressDTO.getIsDefault()){
            unsetDefaultAddress(userId,addressId);
        }
        if (addressDTO.getFirstname() != null && !addressDTO.getFirstname().isEmpty()) {
            address.setFirstname(addressDTO.getFirstname());
        }
        if (addressDTO.getLastname() != null && !addressDTO.getLastname().isEmpty()) {
            address.setLastname(addressDTO.getLastname());
        }
        if (addressDTO.getPhoneNumber() != null && !addressDTO.getPhoneNumber().isEmpty()) {
            address.setPhoneNumber(addressDTO.getPhoneNumber());
        }
        if (addressDTO.getStreet() != null && !addressDTO.getStreet().isEmpty()) {
            address.setStreet(addressDTO.getStreet());
        }
        if (addressDTO.getHouseNumber() != null && !addressDTO.getHouseNumber().isEmpty()) {
            address.setHouseNumber(addressDTO.getHouseNumber());
        }
        if (addressDTO.getCity() != null && !addressDTO.getCity().isEmpty()) {
            address.setCity(addressDTO.getCity());
        }
        if (addressDTO.getCountry() != null && !addressDTO.getCountry().isEmpty()) {
            address.setCountry(addressDTO.getCountry());
        }
        if (addressDTO.getZipCode() != null && !addressDTO.getZipCode().isEmpty()) {
            address.setZipCode(addressDTO.getZipCode());
        }
        if(addressDTO.getIsDefault()!=null){
            address.setDefault(addressDTO.getIsDefault());
        }
        Address updatedAddress = addressRepository.save(address);
        return updatedAddress.getId();
    }

    @Transactional
    public void deleteAddress(Long addressId){
        Long userId = getUserIdFromSecurityContext();
        Optional<Address> optionalDeleteAddress = addressRepository.findById(addressId);
        if(optionalDeleteAddress.isPresent()){
            Address deleteAddress = optionalDeleteAddress.get();
            if(deleteAddress.isDefault()){
                addressRepository.delete(deleteAddress);
               addressRepository.findFirstByUserIdOrderByIdAsc(userId).ifPresent((minimumIdAddress)->{
                   minimumIdAddress.setDefault(true);
                   addressRepository.save(minimumIdAddress);
               });
            }else{
                addressRepository.delete(deleteAddress);
            }
        }
    }

    public List<AddressDTO> getAddresses() {
        Long userId = getUserIdFromSecurityContext();
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        return AddressDTO.addressDTOS(addresses);
    }

    @Transactional
    public Long setDefaultAddress(Long addressId) {
        Long userId = getUserIdFromSecurityContext();
        unsetDefaultAddress(userId,addressId);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address with id " + addressId +" not found"));
        if(!address.getUser().getId().equals(userId)){
            throw new InvalidContentException("Address does not belong to the user");
        }
        address.setDefault(true);
        Address updatedAddress =addressRepository.save(address);
        return updatedAddress.getId();
    }

    private void unsetDefaultAddress(Long userId,Long addressId){
        Optional<Address> optionalDefaultAddress = addressRepository.findAddressByUserIdAndIsDefaultIsTrue(userId);
        if(optionalDefaultAddress.isPresent()){
            Address defaultAddress = optionalDefaultAddress.get();
            if(!defaultAddress.getId().equals(addressId)){
                defaultAddress.setDefault(false);
                addressRepository.save(defaultAddress);
            }
        }
    }
}
