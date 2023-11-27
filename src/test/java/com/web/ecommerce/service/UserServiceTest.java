package com.web.ecommerce.service;

import com.web.ecommerce.configuration.security.JwtService;
import com.web.ecommerce.dto.user.SignInData;
import com.web.ecommerce.dto.user.SignUpData;
import com.web.ecommerce.dto.user.UserDTO;
import com.web.ecommerce.exception.InvalidContentException;
import com.web.ecommerce.model.user.Role;
import com.web.ecommerce.model.user.User;
import com.web.ecommerce.repository.AddressRepository;
import com.web.ecommerce.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static com.web.ecommerce.util.Constant.BAD_CREDENTIALS_RESPONSE_MESSAGE;

public class UserServiceTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final AddressRepository addressRepository = Mockito.mock(AddressRepository.class);
    private final CartService cartService = Mockito.mock(CartService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
    private final JwtService jwtService = Mockito.mock(JwtService.class);
    private final PasswordResetTokenService passwordResetTokenService = Mockito.mock(PasswordResetTokenService.class);
    private final MailSenderService mailSenderService = Mockito.mock(MailSenderService.class);
    private static final String MOCK_EMAIL = "mock@mock.com";
    private static final String MOCK_PASSWORD = "mock";
    private static final String MOCK_ENCRYPTED_PASSWORD = "mockEncryptedPassword";
    private static final String MOCK_FIRSTNAME = "mockFirstname";
    private static final String MOCK_LASTNAME = "mockLastname";
    private static final String MOCK_REGISTER_EMAIL_EXIST_ERROR_MESSAGE = "User with email " + MOCK_EMAIL + " already existed";

    private UserService userService;

    public UserServiceTest() {
    }

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository,
                addressRepository,
                cartService,
                bCryptPasswordEncoder,
                jwtService,
                passwordResetTokenService,
                mailSenderService);
    }
    
    @Test
    @DisplayName("Register user with an existing email")
    public void registerUserWithExistingEmail() {
        SignUpData signUpData = createSignUpData();
        Mockito.when(userRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.of(new User()));
        Assertions.assertThatThrownBy(() -> userService.register(signUpData, ""))
                .isInstanceOf(InvalidContentException.class)
                .hasMessageContaining(MOCK_REGISTER_EMAIL_EXIST_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Register user with new email")
    public void registerUserWithNewEmail() {
        SignUpData signUpData = createSignUpData();
        UserDTO expectedUserDTOResponse = createUserDTO();
        User user = createUser();
        Mockito.when(userRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserDTO actualUserDTOResponse = userService.register(signUpData, "");
        Assertions.assertThat(actualUserDTOResponse).isEqualTo(expectedUserDTOResponse);
    }

    @Test
    @DisplayName("Login with incorrect email")
    public void loginWithIncorrectEmail() {
        SignInData signInData = createSignInData();
        Mockito.when(userRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> userService.login(signInData, ""))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining(BAD_CREDENTIALS_RESPONSE_MESSAGE);
    }

    @Test
    @DisplayName("Login with incorrect password")
    public void loginWithIncorrectPassword() {
        SignInData signInData = createSignInData();
        User user = createUser();
        Mockito.when(userRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches(MOCK_PASSWORD, user.getPassword())).thenReturn(false);
        Assertions.assertThatThrownBy(() -> userService.login(signInData, ""))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining(BAD_CREDENTIALS_RESPONSE_MESSAGE);
    }

    @Test
    @DisplayName("Login with correct credentials")
    public void loginWithCorrectCredentials() {
        SignInData signInData = createSignInData();
        User user = createUser();
        UserDTO expectedUserDTOResponse = createUserDTO();
        Mockito.when(userRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches(MOCK_PASSWORD, user.getPassword())).thenReturn(true);
        UserDTO actualUserDTOResponse = userService.login(signInData, "");
        Assertions.assertThat(actualUserDTOResponse).isEqualTo(expectedUserDTOResponse);
    }

    private SignUpData createSignUpData() {
        SignUpData signUpData = new SignUpData();
        signUpData.setFirstname(UserServiceTest.MOCK_FIRSTNAME);
        signUpData.setLastname(UserServiceTest.MOCK_LASTNAME);
        signUpData.setEmail(UserServiceTest.MOCK_EMAIL);
        signUpData.setPassword(Base64.getEncoder().encodeToString(UserServiceTest.MOCK_PASSWORD.getBytes()));
        return signUpData;
    }

    private SignInData createSignInData() {
        SignInData signInData = new SignInData();
        signInData.setEmail(Base64.getEncoder().encodeToString(MOCK_EMAIL.getBytes()));
        signInData.setPassword(Base64.getEncoder().encodeToString(MOCK_PASSWORD.getBytes()));
        return signInData;
    }

    private UserDTO createUserDTO() {
        String jwt = jwtService.generateToken(Map.of("username", MOCK_EMAIL));
        return UserDTO.builder()
                .firstname(MOCK_FIRSTNAME)
                .lastname(MOCK_LASTNAME)
                .email(MOCK_EMAIL)
                .role(Role.ROLE_USER.name())
                .token(jwt)
                .build();
    }

    private User createUser() {
        Mockito.when(bCryptPasswordEncoder.encode(MOCK_PASSWORD)).thenReturn(MOCK_ENCRYPTED_PASSWORD);
        return User.builder()
                .firstname(MOCK_FIRSTNAME)
                .lastname(MOCK_LASTNAME)
                .email(MOCK_EMAIL)
                .password(bCryptPasswordEncoder.encode(MOCK_PASSWORD))
                .role(Role.ROLE_USER)
                .build();
    }

}