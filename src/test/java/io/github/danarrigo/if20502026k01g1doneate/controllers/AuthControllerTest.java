package io.github.danarrigo.if20502026k01g1doneate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.danarrigo.if20502026k01g1doneate.dtos.DonatorRegistrationRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.LoginRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.RecipientRegistrationRequest;
import io.github.danarrigo.if20502026k01g1doneate.enums.DonatorType;
import io.github.danarrigo.if20502026k01g1doneate.enums.RecipientType;
import io.github.danarrigo.if20502026k01g1doneate.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private AuthService authService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        AuthController authController = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("user1", "password");
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Map.of("username", "user1", "role", "DONATOR"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.role").value("DONATOR"));
    }

    @Test
    void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest("user1", "wrong");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    void testRegisterDonatorSuccess() throws Exception {
        DonatorRegistrationRequest request = new DonatorRegistrationRequest();
        request.setUsername("newdonator");
        request.setPassword("password");
        request.setAddress("Alamat");
        request.setPhoneNumber("08123");
        request.setEmail("donator@example.com");
        request.setDonatorType(DonatorType.CAFE);

        when(authService.registerDonator(any(DonatorRegistrationRequest.class)))
                .thenReturn(Map.of("username", "newdonator", "role", "DONATOR"));

        mockMvc.perform(post("/api/auth/register/donator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newdonator"))
                .andExpect(jsonPath("$.role").value("DONATOR"));
    }

    @Test
    void testRegisterDonatorFailureDuplicateUsername() throws Exception {
        DonatorRegistrationRequest request = new DonatorRegistrationRequest();
        request.setUsername("existing");

        when(authService.registerDonator(any(DonatorRegistrationRequest.class)))
                .thenThrow(new RuntimeException("Username already taken"));

        mockMvc.perform(post("/api/auth/register/donator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already taken"));
    }

    @Test
    void testRegisterRecipientSuccess() throws Exception {
        RecipientRegistrationRequest request = new RecipientRegistrationRequest();
        request.setUsername("newrecipient");
        request.setPassword("password");
        request.setAddress("Alamat");
        request.setPhoneNumber("08222");
        request.setEmail("recipient@example.com");
        request.setFullName("Recipient Name");
        request.setOperationalTimeStart(LocalTime.of(8, 0));
        request.setOperationalTimeEnd(LocalTime.of(16, 0));
        request.setRecipientType(RecipientType.ORGANIZATION);

        when(authService.registerRecipient(any(RecipientRegistrationRequest.class)))
                .thenReturn(Map.of("username", "newrecipient", "role", "RECIPIENT"));

        mockMvc.perform(post("/api/auth/register/recipient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newrecipient"))
                .andExpect(jsonPath("$.role").value("RECIPIENT"));
    }

    @Test
    void testRegisterRecipientFailureDuplicateUsername() throws Exception {
        RecipientRegistrationRequest request = new RecipientRegistrationRequest();
        request.setUsername("existingrecipient");

        when(authService.registerRecipient(any(RecipientRegistrationRequest.class)))
                .thenThrow(new RuntimeException("Username already taken"));

        mockMvc.perform(post("/api/auth/register/recipient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already taken"));
    }
}
