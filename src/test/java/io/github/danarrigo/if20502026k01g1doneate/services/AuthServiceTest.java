package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.DonatorRegistrationRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.LoginRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.RecipientRegistrationRequest;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.enums.DonatorType;
import io.github.danarrigo.if20502026k01g1doneate.enums.RecipientType;
import io.github.danarrigo.if20502026k01g1doneate.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.github.danarrigo.if20502026k01g1doneate.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private Donator donator;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("donator1", "password123");
        donator = new Donator(
                "donator1",
                "password123",
                "Jl. Mawar 1",
                "08123",
                "donator@example.com",
                DonatorType.RESTAURANT
        );
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByUsername("donator1")).thenReturn(Optional.of(donator));
        when(passwordEncoder.matches("password123", "password123")).thenReturn(true);
        when(jwtUtils.generateToken("donator1")).thenReturn("mock-token");

        Map<String, String> result = authService.login(loginRequest);

        assertEquals("donator1", result.get("username"));
        assertEquals("DONATOR", result.get("role"));
        assertEquals("mock-token", result.get("token"));
    }

    @Test
    void testLoginInvalidPassword() {
        when(userRepository.findByUsername("donator1")).thenReturn(Optional.of(donator));
        when(passwordEncoder.matches("wrong-password", "password123")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(new LoginRequest("donator1", "wrong-password")));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(new LoginRequest("unknown", "password123")));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testRegisterDonatorSuccess() {
        DonatorRegistrationRequest request = createDonatorRequest("newdonator");

        when(userRepository.existsByUsername("newdonator")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(jwtUtils.generateToken("newdonator")).thenReturn("mock-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> result = authService.registerDonator(request);

        assertEquals("newdonator", result.get("username"));
        assertEquals("DONATOR", result.get("role"));
        assertEquals("mock-token", result.get("token"));
        verify(userRepository, times(1)).save(any(Donator.class));
    }

    @Test
    void testRegisterDonatorDuplicateUsername() {
        DonatorRegistrationRequest request = createDonatorRequest("existing");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.registerDonator(request));

        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterRecipientSuccess() {
        RecipientRegistrationRequest request = createRecipientRequest("newrecipient");

        when(userRepository.existsByUsername("newrecipient")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(jwtUtils.generateToken("newrecipient")).thenReturn("mock-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> result = authService.registerRecipient(request);

        assertEquals("newrecipient", result.get("username"));
        assertEquals("RECIPIENT", result.get("role"));
        assertEquals("mock-token", result.get("token"));
        verify(userRepository, times(1)).save(any(Recipient.class));
    }

    @Test
    void testRegisterRecipientDuplicateUsername() {
        RecipientRegistrationRequest request = createRecipientRequest("existingrecipient");

        when(userRepository.existsByUsername("existingrecipient")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.registerRecipient(request));

        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    private DonatorRegistrationRequest createDonatorRequest(String username) {
        DonatorRegistrationRequest request = new DonatorRegistrationRequest();
        request.setUsername(username);
        request.setPassword("pass123");
        request.setAddress("Jl. Melati 2");
        request.setPhoneNumber("08111");
        request.setEmail("newdonator@example.com");
        request.setDonatorType(DonatorType.CAFE);
        return request;
    }

    private RecipientRegistrationRequest createRecipientRequest(String username) {
        RecipientRegistrationRequest request = new RecipientRegistrationRequest();
        request.setUsername(username);
        request.setPassword("pass321");
        request.setAddress("Jl. Anggrek 3");
        request.setPhoneNumber("08222");
        request.setEmail("recipient@example.com");
        request.setFullName("Recipient Name");
        request.setOperationalTimeStart(LocalTime.of(8, 0));
        request.setOperationalTimeEnd(LocalTime.of(16, 0));
        request.setRecipientType(RecipientType.ORGANIZATION);
        return request;
    }
}