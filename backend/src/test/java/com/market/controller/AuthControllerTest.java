package com.market.controller;

import com.market.dto.AuthResponse;
import com.market.dto.LoginRequest;
import com.market.dto.RegisterRequest;
import com.market.entity.User;
import com.market.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 认证控制器测试
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private AuthResponse.UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new AuthResponse.UserDTO(1L, "testuser", "test@example.com", null);

        registerRequest = new RegisterRequest();
        registerRequest.setName("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setName("testuser");
        loginRequest.setPassword("password123");

        authResponse = AuthResponse.success("登录成功", "test-token", userDTO);
    }

    @Test
    void testRegister() {
        // Arrange
        when(userService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testLogin() {
        // Arrange
        when(userService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(userService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void testGetCurrentUser() {
        // Act
        ResponseEntity<?> response = authController.getCurrentUser();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }
}
