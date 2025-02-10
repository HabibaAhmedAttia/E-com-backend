package task.mentorship.application.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import task.mentorship.application.dto.LoginRequest;
import task.mentorship.application.dto.RegisterRequest;
import task.mentorship.application.entity.Role;
import task.mentorship.application.entity.User;
import task.mentorship.application.exception.CustomException;
import task.mentorship.application.repository.RoleRepository;
import task.mentorship.application.repository.UserRepository;
import task.mentorship.application.service.UserService;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)

public class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role clientRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setName("Test User");

        clientRole = new Role();
        clientRole.setName(Role.RoleName.CLIENT);

        adminRole = new Role();
        adminRole.setName(Role.RoleName.ADMIN);
    }

    @Test
    void testRegisterUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("Test User");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_password");
        when(roleRepository.findByName(Role.RoleName.CLIENT)).thenReturn(Optional.of(clientRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(request);

        assertNotNull(registeredUser);
        assertEquals(request.getEmail(), registeredUser.getEmail());
        assertEquals(request.getName(), registeredUser.getName());
        assertTrue(registeredUser.getRoles().contains(clientRole));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(CustomException.class, () -> userService.registerUser(request));
    }



    @Test
    void testEmailExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertTrue(userService.emailExists("test@example.com"));

        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);
        assertFalse(userService.emailExists("nonexistent@example.com"));
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, "Updated Name");

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> userService.updateUser(2L, "Updated Name"));
    }

    @Test
    void testAuthenticateUser_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        User authenticatedUser = userService.authenticate(request);

        assertNotNull(authenticatedUser);
        assertEquals(request.getEmail(), authenticatedUser.getEmail());
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.authenticate(request));
    }

    @Test
    void testAuthenticateUser_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.authenticate(request));
    }

}
