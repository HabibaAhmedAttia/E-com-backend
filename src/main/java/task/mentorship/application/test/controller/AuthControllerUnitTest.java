package task.mentorship.application.test.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import task.mentorship.application.controller.UserController;
import task.mentorship.application.dto.RegisterRequest;
import task.mentorship.application.entity.User;
import task.mentorship.application.exception.CustomException;
import task.mentorship.application.security.JwtTokenProvider;
import task.mentorship.application.service.AuthService;
import task.mentorship.application.service.UserService;

import java.util.Map;

@ExtendWith(MockitoExtension.class)

public class AuthControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("bbbbbb");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        when(userService.emailExists(registerRequest.getEmail())).thenReturn(false);
        doNothing().when(userService).registerUser(registerRequest);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());
                //.andExpect(content().string("User registered successfully"));
    }



    @Test
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("bbbbbb");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        when(userService.emailExists(registerRequest.getEmail())).thenReturn(true);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
                //.andExpect(content().string("Email already in use"));
    }

    @Test
    void testLogin_Success() throws Exception {
        String email = "test@example.com";
        String password = "password";
        String token = "mocked_jwt_token";

        when(authService.authenticate(email, password)).thenReturn(token);

        mockMvc.perform(post("/api/login")
                        .header("email", email)
                        .header("password", password))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void testLogin_Failure() throws Exception {
        String email = "test@example.com";
        String password = "wrongpassword";

        when(authService.authenticate(email, password)).thenThrow(new CustomException("Invalid email or password"));

        mockMvc.perform(post("/api/login")
                        .header("email", email)
                        .header("password", password))
                .andExpect(status().isUnauthorized());
                //.andExpect(content().string("Invalid email or password"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        String token = "Bearer mock_token";
        Long userId = 1L;
        String name = "Updated Name";
        User updatedUser = new User();
        updatedUser.setName(name);

        when(jwtTokenProvider.getUsername(anyString())).thenReturn("test@example.com");
        when(userService.updateUser(userId, name)).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("user_id", userId, "name", name))))
                .andExpect(status().isOk());
                //.andExpect(content().string("User updated successfully"))
                //.andExpect(content().string(name));
    }

    @Test
    void testUpdateUser_InvalidToken() throws Exception {
        String token = "Bearer invalid_token";

        when(jwtTokenProvider.getUsername(anyString())).thenThrow(new CustomException("Invalid token"));

        mockMvc.perform(put("/api/users/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("user_id", 1L, "name", "New Name"))))
                .andExpect(status().isBadRequest());
                //.andExpect(content().string("Invalid token"));
    }
	
	
	

}




