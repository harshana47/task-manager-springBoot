package org.example.taskmanager03.controller;

import org.example.taskmanager03.dto.UserDTO;
import org.example.taskmanager03.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.example.taskmanager03.security.JwtAuthenticationFilter;
import org.example.taskmanager03.security.SecurityConfig;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = { SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class },
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthenticationFilter.class, SecurityConfig.class })
)
@Import(UserControllerTest.TestConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        UserService userService() { return Mockito.mock(UserService.class); }
    }

    @Test
    @DisplayName("POST /api/users creates a user")
    void createUser_ok() throws Exception {
        UserDTO req = new UserDTO(null, "user@example.com", "user1", "secret", "ROLE_USER");
        UserDTO res = new UserDTO(UUID.randomUUID(), req.getEmail(), req.getUsername(), null, req.getRole());
        given(userService.createUser(any(UserDTO.class))).willReturn(res);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("GET /api/users lists users")
    void listUsers_ok() throws Exception {
        UserDTO u = new UserDTO(UUID.randomUUID(), "user@example.com", "user1", null, "ROLE_USER");
        given(userService.getAllUsers()).willReturn(List.of(u));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user@example.com"));
    }
}
