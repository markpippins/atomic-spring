// package com.angrysurfer.atomic.login;

// import com.angrysurfer.atomic.broker.Broker;
// import com.angrysurfer.atomic.broker.api.ServiceRequest;
// import com.angrysurfer.atomic.broker.api.ServiceResponse;
// import com.angrysurfer.atomic.user.UserRegistrationDTO;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.core.ValueOperations;

// import java.util.Map;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static org.mockito.Mockito.lenient;

// @ExtendWith(MockitoExtension.class)
// class LoginServiceTest {

//     @Mock
//     private Broker broker;

//     @Mock
//     private RedisTemplate<String, Object> redisTemplate;

//     @Mock
//     private ValueOperations<String, Object> valueOperations;

//     private LoginService loginService;

//     @BeforeEach
//     void setUp() {
//         lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//         loginService = new LoginService(broker, redisTemplate);
//     }

//     @Test
//     void testLoginSuccess() {
//         // Arrange
//         String alias = "testuser";
//         String password = "password123";
        
//         UserRegistrationDTO user = new UserRegistrationDTO();
//         user.setId("1");
//         user.setAlias("testuser");
//         user.setAvatarUrl("http://example.com/avatar.jpg");
        
//         ServiceResponse<UserRegistrationDTO> userValidationResponse = (ServiceResponse<UserRegistrationDTO>)ServiceResponse.ok(user, "validate-id");
//         doReturn(userValidationResponse).when(broker).submit(any(ServiceRequest.class));
        
//         doNothing().when(valueOperations).set(anyString(), any(), any());

//         // Act
//         ServiceResponse<LoginResponse> response = loginService.login(alias, password);

//         // Assert
//         assertTrue(response.isOk());
//         assertNotNull(response.getData());
//         assertTrue(response.getData().isOk());
//         assertNotNull(response.getData().getToken());
        
//         // Verify the broker was called with correct parameters
//         ArgumentCaptor<ServiceRequest> requestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
//         verify(broker).submit(requestCaptor.capture());
        
//         ServiceRequest capturedRequest = requestCaptor.getValue();
//         assertEquals("userAccessService", capturedRequest.getService());
//         assertEquals("validateUser", capturedRequest.getOperation());
//         assertTrue(capturedRequest.getParams().containsKey("alias"));
//         assertTrue(capturedRequest.getParams().containsKey("identifier"));
        
//         // Verify Redis operation
//         verify(valueOperations).set(anyString(), eq(user), any());
//     }

//     @Test
//     @SuppressWarnings("unchecked")
//     void testLoginFailureInvalidCredentials() {
//         // Arrange
//         String alias = "testuser";
//         String password = "wrongpassword";
        
//         // Mock the broker response for invalid credentials
//         ServiceResponse<UserRegistrationDTO> errorResponse = new ServiceResponse<>();
//         errorResponse.setOk(false);
//         errorResponse.setData(null);
//         errorResponse.addError("credentials", "invalid alias or password");
        
//         doReturn(errorResponse).when(broker).submit(any(ServiceRequest.class));
        
//         // Act
//         ServiceResponse<LoginResponse> response = loginService.login(alias, password);
        
//         // Assert
//         assertFalse(response.isOk());
//         assertNotNull(response.getData());
//         LoginResponse loginResponse = response.getData();
//         assertFalse(loginResponse.isOk());
//         assertNotNull(loginResponse.getMessage());
//         assertFalse(loginResponse.getErrors().isEmpty());
        
//         // Verify broker was called with correct parameters
//         ArgumentCaptor<ServiceRequest> requestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
//         verify(broker).submit(requestCaptor.capture());
        
//         ServiceRequest capturedRequest = requestCaptor.getValue();
//         assertEquals("userAccessService", capturedRequest.getService());
//         assertEquals("validateUser", capturedRequest.getOperation());
//         assertEquals(alias, capturedRequest.getParams().get("alias"));
//         assertEquals(password, capturedRequest.getParams().get("identifier"));
        
//         // Verify no Redis operations were performed for failed login
//         verify(redisTemplate.opsForValue(), never()).set(anyString(), any(), any());
//     }
    
//     @Test
//     void testLoginFailureNullResponseData() {
//         // Arrange
//         String alias = "testuser";
//         String password = "password123";
        
//         ServiceResponse<UserRegistrationDTO> userValidationResponse = 
//             (ServiceResponse<UserRegistrationDTO>)ServiceResponse.ok((UserRegistrationDTO)null, "validate-id");
//         doReturn(userValidationResponse).when(broker).submit(any(ServiceRequest.class));

//         // Act
//         ServiceResponse<LoginResponse> response = loginService.login(alias, password);

//         // Assert
//         assertFalse(response.isOk());
//         assertNotNull(response.getData());
//         assertFalse(response.getData().isOk());
//         assertTrue(response.getData().getMessage().contains("invalid credentials"));
//     }

//     @Test
//     void testLoginBrokerException() {
//         // Arrange
//         String alias = "testuser";
//         String password = "password123";
        
//         doThrow(new RuntimeException("Broker error")).when(broker).submit(any(ServiceRequest.class));

//         // Act
//         ServiceResponse<LoginResponse> response = loginService.login(alias, password);

//         // Assert
//         assertFalse(response.isOk());
//         assertNotNull(response.getData());
//         // In case of exception, expect a LoginResponse with error details
//         assertTrue(response.getData().getMessage().contains("Broker error"));
//     }

//     @Test
//     void testLogoutSuccess() {
//         // Arrange
//         String token = UUID.randomUUID().toString();
//         when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//         when(valueOperations.get("user:" + token)).thenReturn(new UserRegistrationDTO());
//         when(redisTemplate.delete("user:" + token)).thenReturn(true);

//         // Act
//         ServiceResponse<Boolean> response = loginService.logout(token);

//         // Assert
//         assertTrue(response.isOk());
//         assertNotNull(response.getData());
//         assertTrue(response.getData());
        
//         verify(redisTemplate).delete("user:" + token);
//     }

//     @Test
//     void testLogoutInvalidTokenFormat() {
//         // Arrange
//         String invalidToken = "invalid-uuid-format";

//         // Act
//         ServiceResponse<Boolean> response = loginService.logout(invalidToken);

//         // Assert
//         assertFalse(response.isOk());
//         assertNotNull(response.getData());
//         assertFalse(response.getData());
//         assertNotNull(response.getErrors());
//         assertTrue(response.getErrors().stream()
//             .anyMatch(error -> error.get("message").toString().contains("Invalid token format")));
//     }

//     @Test
//     void testLogoutTokenNotFound() {
//         // Arrange
//         String token = UUID.randomUUID().toString();
//         lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//         lenient().when(valueOperations.get("user:" + token)).thenReturn(null);
//         lenient().when(redisTemplate.delete("user:" + token)).thenReturn(false);

//         // Act
//         ServiceResponse<Boolean> response = loginService.logout(token);

//         // Assert
//         assertTrue(response.isOk()); // Operation itself succeeded, but token wasn't found
//         assertNotNull(response.getData());
//         assertFalse(response.getData()); // Token was not found to delete
//     }

//     @Test
//     void testIsLoggedInSuccess() {
//         // Arrange
//         String token = UUID.randomUUID().toString();
//         UserRegistrationDTO user = new UserRegistrationDTO();
//         when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//         when(valueOperations.get("user:" + token)).thenReturn(user);

//         // Act
//         ServiceResponse<Boolean> response = loginService.isLoggedIn(token);

//         // Assert
//         assertTrue(response.isOk());
//         assertNotNull(response.getData());
//         assertTrue(response.getData());
//     }

//     @Test
//     void testIsLoggedInFalse() {
//         // Arrange
//         String token = UUID.randomUUID().toString();
//         when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//         when(valueOperations.get("user:" + token)).thenReturn(null);

//         // Act
//         ServiceResponse<Boolean> response = loginService.isLoggedIn(token);

//         // Assert
//         assertTrue(response.isOk());
//         assertNotNull(response.getData());
//         assertFalse(response.getData());
//     }

//     @Test
//     void testIsLoggedInInvalidToken() {
//         // Arrange
//         String invalidToken = "invalid-uuid-format";

//         // Act
//         ServiceResponse<Boolean> response = loginService.isLoggedIn(invalidToken);

//         // Assert
//         assertFalse(response.isOk());
//         assertNotNull(response.getData());
//         assertFalse(response.getData());
//         assertNotNull(response.getErrors());
//         assertTrue(response.getErrors().stream()
//             .anyMatch(error -> error.get("message").toString().contains("Invalid token format")));
//     }

//     @Test
//     void testGetUserRegistrationForTokenSuccess() {
//         // Arrange
//         String token = UUID.randomUUID().toString();
//         UserRegistrationDTO user = new UserRegistrationDTO();
//         user.setAlias("testuser");
//         when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//         when(valueOperations.get("user:" + token)).thenReturn(user);

//         // Act
//         ServiceResponse<UserRegistrationDTO> response = loginService.getUserRegistrationForToken(token);

//         // Assert
//         assertTrue(response.isOk());
//         assertNotNull(response.getData());
//         assertEquals(user, response.getData());
//     }

//     @Test
//     void testGetUserRegistrationForTokenNotFound() {
//         // Arrange
//         String token = UUID.randomUUID().toString();
//         when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//         when(valueOperations.get("user:" + token)).thenReturn(null);

//         // Act
//         ServiceResponse<UserRegistrationDTO> response = loginService.getUserRegistrationForToken(token);

//         // Assert
//         assertFalse(response.isOk());
//         assertNull(response.getData());
//         assertNotNull(response.getErrors());
//         assertTrue(response.getErrors().stream()
//             .anyMatch(error -> error.get("message").toString().contains("Token not found or expired")));
//     }

//     @Test
//     void testGetUserRegistrationForTokenInvalidFormat() {
//         // Arrange
//         String invalidToken = "invalid-uuid-format";

//         // Act
//         ServiceResponse<UserRegistrationDTO> response = loginService.getUserRegistrationForToken(invalidToken);

//         // Assert
//         assertFalse(response.isOk());
//         assertNull(response.getData());
//         assertNotNull(response.getErrors());
//         assertTrue(response.getErrors().stream()
//             .anyMatch(error -> error.get("message").toString().contains("Invalid token format")));
//     }

//     @Test
//     void testGetLoggedInUser() {
//         // Arrange
//         UUID token = UUID.randomUUID();
//         UserRegistrationDTO expectedUser = new UserRegistrationDTO();
//         expectedUser.setAlias("testuser");
//         when(valueOperations.get("user:" + token.toString())).thenReturn(expectedUser);

//         // Act
//         UserRegistrationDTO result = loginService.getLoggedInUser(token);

//         // Assert
//         assertEquals(expectedUser, result);
//     }

//     @Test
//     void testGetLoggedInUserNotFound() {
//         // Arrange
//         UUID token = UUID.randomUUID();
//         when(valueOperations.get("user:" + token.toString())).thenReturn(null);

//         // Act
//         UserRegistrationDTO result = loginService.getLoggedInUser(token);

//         // Assert
//         assertNull(result);
//     }
// }