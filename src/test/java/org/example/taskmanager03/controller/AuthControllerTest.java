package org.example.taskmanager03.controller;

import org.example.taskmanager03.dto.AuthRequest;
import org.example.taskmanager03.entity.User;
import org.example.taskmanager03.repo.UserRepository;
import org.example.taskmanager03.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        AuthenticationManager authenticationManager() { return Mockito.mock(AuthenticationManager.class); }

        @Bean
        JwtUtil jwtUtil() { return Mockito.mock(JwtUtil.class); }

        @Bean
        UserRepository userRepository() { return Mockito.mock(UserRepository.class); }
    }

    @Test
    @DisplayName("POST /api/auth/login returns JWT on success")
    void login_success() throws Exception {
        // Arrange
        String email = "admin@example.com";
        String password = "secret";
        AuthRequest req = new AuthRequest(email, password);

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(auth);

        User user = new User();
        user.setEmail(email);
        user.setUsername("admin");
        user.setRole("ROLE_ADMIN");
        given(userRepository.findByEmail(eq(email))).willReturn(Optional.of(user));
        given(jwtUtil.generateToken(eq(email), eq("ROLE_ADMIN"))).willReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/login returns 401 on bad credentials")
    void login_badCredentials() throws Exception {
        String email = "admin@example.com";
        String password = "wrong";
        AuthRequest req = new AuthRequest(email, password);

        doThrow(new BadCredentialsException("bad")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
