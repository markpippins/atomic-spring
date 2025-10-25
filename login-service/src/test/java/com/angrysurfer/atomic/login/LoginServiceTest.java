// package com.angrysurfer.atomic.login;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import com.angrysurfer.atomic.user.UserDTO;
// import com.angrysurfer.atomic.user.service.UserAccessService;

// @ExtendWith(MockitoExtension.class)
// class LoginServiceTest {

//     @Mock
//     private UserAccessService userAccessService;

//     private LoginService loginService;
//     private UserDTO userDTO;

//     @BeforeEach
//     void setUp() {
//         loginService = new LoginService(userAccessService);
        
//         userDTO = new UserDTO();
//         userDTO.setId("123");
//         userDTO.setAlias("testUser");
//         userDTO.setEmail("test@example.com");
//         userDTO.setIdentifier("password123");
//     }

//     @Test
//     void testLoginSuccess() {
//         when(userAccessService.login("testUser", "password123")).thenReturn(userDTO);

//         var response = loginService.login("testUser", "password123");

//         assertTrue(response.isOk());
//         assertNotNull(response.getData());
//         assertTrue(response.getData().isOk());
//         assertEquals("SUCCESS", response.getData().getToken());
//         assertEquals(userDTO, response.getData().getUser());
//     }

//     @Test
//     void testLoginFailureWrongPassword() {
//         when(userAccessService.login("testUser", "wrongPassword")).thenReturn(null);

//         var response = loginService.login("testUser", "wrongPassword");

//         assertFalse(response.isOk()); // Service call failed because login failed
//         assertNotNull(response.getData());
//         assertFalse(response.getData().isOk()); // Login failed
//         assertEquals("FAILURE", response.getData().getToken());
//         assertTrue(response.getData().getErrors().containsKey("identifier"));
//     }

//     @Test
//     void testLoginFailureUserNotFound() {
//         when(userAccessService.login("nonexistent", "anyPassword")).thenReturn(null);

//         var response = loginService.login("nonexistent", "anyPassword");

//         assertFalse(response.isOk()); // Service call failed because login failed
//         assertNotNull(response.getData());
//         assertFalse(response.getData().isOk()); // Login failed
//         assertEquals("FAILURE", response.getData().getToken());
//         assertTrue(response.getData().getErrors().containsKey("identifier"));
//     }

//     @Test
//     void testLoginWithException() {
//         when(userAccessService.login("testUser", "password123")).thenThrow(new RuntimeException("Database error"));

//         var response = loginService.login("testUser", "password123");

//         assertFalse(response.isOk()); // Service call failed due to exception
//         assertNotNull(response.getData());
//         assertEquals("FAILURE", response.getData().getToken());
//     }
// }