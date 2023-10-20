package com.web.ecommerce.service;

import com.web.ecommerce.configuration.security.JwtService;
import com.web.ecommerce.dto.user.*;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.exception.ResourceNotFoundException;
import com.web.ecommerce.model.user.Address;
import com.web.ecommerce.model.user.Role;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.AddressRepository;
import com.web.ecommerce.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.web.ecommerce.util.Constant.WEBSITE_URL;
import static com.web.ecommerce.util.Util.getUserIdFromSecurityContext;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final MailSenderService mailSenderService;

    @Autowired
    public UserService(UserRepository userRepository,
                       AddressRepository addressRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService, PasswordResetTokenService passwordResetTokenService, MailSenderService mailSenderService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.mailSenderService = mailSenderService;
    }

    @Transactional
    public UserDTO register(SignUpRequest signUpRequest) {
        byte[] decodedPasswordBytes = Base64.getDecoder().decode(signUpRequest.getPassword());
        String decodedPassword = new String(decodedPasswordBytes);
        Optional<User> dbUser = userRepository.findByEmail(signUpRequest.getEmail());
        if (dbUser.isPresent()) {
            throw new InvalidContentException("User with email " + signUpRequest.getEmail() + " already existed");
        }
        User user = User.builder()
                .firstname(signUpRequest.getFirstname())
                .lastname(signUpRequest.getLastname())
                .email(signUpRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(decodedPassword))
                .role(Role.ROLE_USER)
                .build();
        User savedUser = userRepository.save(user);
        String jwt = jwtService.generateToken(Map.of("username", signUpRequest.getEmail()));
        UserDTO userDTO = UserDTO.builder()
                .firstname(savedUser.getFirstname())
                .lastname(savedUser.getLastname())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .token(jwt)
                .build();
        return userDTO;
    }

    @Transactional
    public UserDTO login(SignInRequest signInRequest) {
        byte[] decodedEmailBytes = Base64.getDecoder().decode(signInRequest.getEmail());
        String decodedEmail = new String(decodedEmailBytes);
        byte[] decodedPasswordBytes = Base64.getDecoder().decode(signInRequest.getPassword());
        String decodedPassword = new String(decodedPasswordBytes);
        User user = userRepository.findByEmail(decodedEmail)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials."));
        if (bCryptPasswordEncoder.matches(decodedPassword, user.getPassword())) {
            String jwt = jwtService.generateToken(Map.of("username", decodedEmail));
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
        if (address.getIsDefault() != null && address.getIsDefault()) {
            unsetDefaultAddress(userId, -1L);
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
    public Long updateAddress(AddressDTO addressDTO, Long addressId) {
        Long userId = getUserIdFromSecurityContext();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " not found"));
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault()) {
            unsetDefaultAddress(userId, addressId);
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
        if (addressDTO.getIsDefault() != null) {
            address.setDefault(addressDTO.getIsDefault());
        }
        Address updatedAddress = addressRepository.save(address);
        return updatedAddress.getId();
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        Long userId = getUserIdFromSecurityContext();
        Optional<Address> optionalDeleteAddress = addressRepository.findById(addressId);
        if (optionalDeleteAddress.isPresent()) {
            Address inactiveAddress = optionalDeleteAddress.get();
            inactiveAddress.setActive(false);
            if (inactiveAddress.isDefault()) {
                inactiveAddress.setDefault(false);
                addressRepository.findFirstByUserIdAndIdNotOrderByIdAsc(userId, addressId).ifPresent((minimumIdAddress) -> {
                    minimumIdAddress.setDefault(true);
                    addressRepository.save(minimumIdAddress);
                });
            }
            addressRepository.save(inactiveAddress);
        }
    }

    public List<AddressDTO> getAddresses() {
        Long userId = getUserIdFromSecurityContext();
        List<Address> addresses = addressRepository.findAllByUserIdAndIsActiveIsTrue(userId, Sort.by("isDefault").descending());
        return AddressDTO.addressDTOS(addresses);
    }

    @Transactional
    public Long setDefaultAddress(Long addressId) {
        Long userId = getUserIdFromSecurityContext();
        unsetDefaultAddress(userId, addressId);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " not found"));
        if (!address.getUser().getId().equals(userId)) {
            throw new InvalidContentException("Address does not belong to the user");
        }
        address.setDefault(true);
        Address updatedAddress = addressRepository.save(address);
        return updatedAddress.getId();
    }

    private void unsetDefaultAddress(Long userId, Long addressId) {
        Optional<Address> optionalDefaultAddress = addressRepository.findAddressByUserIdAndIsDefaultIsTrue(userId);
        if (optionalDefaultAddress.isPresent()) {
            Address defaultAddress = optionalDefaultAddress.get();
            if (!defaultAddress.getId().equals(addressId)) {
                defaultAddress.setDefault(false);
                addressRepository.save(defaultAddress);
            }
        }
    }

    @Transactional
    public Long updateDetails(UpdateDetailsRequest detailsRequest) {
        Long userId = getUserIdFromSecurityContext();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFirstname(detailsRequest.getFirstname());
        user.setLastname(detailsRequest.getLastname());
        User updatedUser = userRepository.save(user);
        return updatedUser.getId();
    }

    @Transactional
    public void updatePassword(NewPasswordRequest passwordRequest) {
        Long userId = getUserIdFromSecurityContext();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        byte[] decodedBytes = Base64.getDecoder().decode(passwordRequest.getPassword());
        String decodedPassword = new String(decodedBytes);
        String encryptedPassword = bCryptPasswordEncoder.encode(decodedPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }

    @Transactional
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist"));
        String passwordResetToken = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(user, passwordResetToken);
        String url = WEBSITE_URL + "/resetpassword?token=" + passwordResetToken;
        try {
            mailSenderService.sendPasswordResetEmail(user, url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return passwordResetToken;
    }

    @Transactional
    public String resetPassword(NewPasswordRequest passwordRequest,String token) {
        String valid = passwordResetTokenService.validatePasswordResetToken(token);
        if(!valid.equalsIgnoreCase("valid")){
            throw new InvalidContentException("Bad request");
        }
        User user = userRepository.findUserByPasswordResetToken(token).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        byte[] decodedPasswordBytes = Base64.getDecoder().decode(passwordRequest.getPassword());
        String decodedPassword = new String(decodedPasswordBytes);
        user.setPassword(bCryptPasswordEncoder.encode(decodedPassword));
        userRepository.save(user);
        return "Password has been reset";
    }

    @Transactional
    public String verifyPasswordResetToken(String token) {
        return passwordResetTokenService.validatePasswordResetToken(token);
    }
}
