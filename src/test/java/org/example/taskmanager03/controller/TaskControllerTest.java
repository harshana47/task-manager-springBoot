package org.example.taskmanager03.controller;

import org.example.taskmanager03.dto.TaskDTO;
import org.example.taskmanager03.service.TaskService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.example.taskmanager03.security.JwtAuthenticationFilter;
import org.example.taskmanager03.security.SecurityConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class,
        excludeAutoConfiguration = { SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class },
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtAuthenticationFilter.class, SecurityConfig.class })
)
@Import(TaskControllerTest.TestConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        TaskService taskService() { return Mockito.mock(TaskService.class); }
    }

    @Test
    @DisplayName("POST /api/tasks creates a task")
    void createTask_ok() throws Exception {
        TaskDTO req = new TaskDTO(null, "Title", "Desc", "HIGH", "OPEN", LocalDate.now(), null);
        TaskDTO res = new TaskDTO(UUID.randomUUID(), req.getTitle(), req.getDescription(), req.getPriority(), req.getStatus(), req.getDueDate(), null);
        given(taskService.createTask(any(TaskDTO.class))).willReturn(res);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns a task")
    void getTask_ok() throws Exception {
        UUID id = UUID.randomUUID();
        TaskDTO res = new TaskDTO(id, "Title", "Desc", "HIGH", "OPEN", LocalDate.now(), null);
        given(taskService.getTaskById(eq(id))).willReturn(res);

        mockMvc.perform(get("/api/tasks/{uuid}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(id.toString()));
    }

    @Test
    @DisplayName("GET /api/tasks paginates")
    void listTasks_ok() throws Exception {
        TaskDTO t = new TaskDTO(UUID.randomUUID(), "Title", "Desc", "HIGH", "OPEN", LocalDate.now(), null);
        Page<TaskDTO> page = new PageImpl<>(List.of(t));
        given(taskService.getTasks(anyInt(), anyInt(), any(), any(), any(), anyString())).willReturn(page);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }
}
