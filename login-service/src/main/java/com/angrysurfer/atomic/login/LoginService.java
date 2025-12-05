package com.angrysurfer.atomic.login;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.Broker;
import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.user.UserRegistrationDTO;

@Service("loginService")
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final Broker broker;
    private final RedisTemplate<String, Object> redisTemplate;

    public LoginService(Broker broker, RedisTemplate<String, Object> redisTemplate) {
        this.broker = broker;
        this.redisTemplate = redisTemplate;
        log.info("LoginService initialized with broker and Redis integration");
    }

    @BrokerOperation("login")
    public ServiceResponse<LoginResponse> login(@BrokerParam("alias") String alias,
            @BrokerParam("identifier") String password) {

        log.info("Login user {}", alias);

        ServiceResponse<LoginResponse> serviceResponse = new ServiceResponse<>();

        try {
            // Call the user-access-service through the broker to validate the user
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("alias", alias);
            params.put("identifier", password);
            
            ServiceRequest request = new ServiceRequest(
                "userAccessService", 
                "validateUser", 
                params, 
                "validate-user-" + System.currentTimeMillis()
            );
            
            @SuppressWarnings("unchecked")
            ServiceResponse<UserRegistrationDTO> userValidationResponse = 
                (ServiceResponse<UserRegistrationDTO>) broker.submit(request);

            if (!userValidationResponse.isOk() || userValidationResponse.getData() == null) {
                LoginResponse response = new LoginResponse("FAILURE", "invalid credentials");
                response.setOk(false);
                response.addError("credentials", "invalid alias or password");

                serviceResponse.setOk(false);
                serviceResponse.setData(response);
                return serviceResponse;
            }

            UserRegistrationDTO user = userValidationResponse.getData();

            // Generate UUID token for successful login
            UUID token = UUID.randomUUID();
            
            LoginResponse response = new LoginResponse("SUCCESS", null);
            response.setToken(token.toString());
            response.setUserId(user.getId());
            response.setAvatarUrl(user.getAvatarUrl());
            response.setAdmin(user.isAdmin());
            response.setOk(true);
            
            // Store user in Redis with TTL (e.g., 24 hours)
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ops.set("user:" + token.toString(), user, java.time.Duration.ofHours(24));

            serviceResponse.setData(response);
            serviceResponse.setOk(true);
            return serviceResponse;

        } catch (Exception e) {
            log.error("Error during login:", e);
            serviceResponse.setData(new LoginResponse("FAILURE", e.getMessage()));
            serviceResponse.setOk(false);
            return serviceResponse;
        }

    }

    @BrokerOperation("logout")
    public ServiceResponse<Boolean> logout(@BrokerParam("token") String token) {
        log.info("Logout user with token {}", token);
        
        ServiceResponse<Boolean> serviceResponse = new ServiceResponse<>();
        
        try {
            // Validate token format
            UUID userToken = UUID.fromString(token);
            
            // Remove user from Redis
            String key = "user:" + token;
            Object user = redisTemplate.opsForValue().get(key);
            boolean removed = user != null && redisTemplate.delete(key);
            
            serviceResponse.setData(removed);
            serviceResponse.setOk(true);
            
            log.info("Logout {} for token {}", removed ? "successful" : "failed (token not found)", token);
            return serviceResponse;
            
        } catch (IllegalArgumentException e) {
            serviceResponse.setData(false);
            serviceResponse.setOk(false);
            serviceResponse.addError("token", "Invalid token format");
            return serviceResponse;
        } catch (Exception e) {
            serviceResponse.setData(false);
            serviceResponse.setOk(false);
            log.error("Error during logout:", e);
            return serviceResponse;
        }
    }

    @BrokerOperation("isLoggedIn")
    public ServiceResponse<Boolean> isLoggedIn(@BrokerParam("token") String token) {
        log.debug("Checking login status for token {}", token);
        
        ServiceResponse<Boolean> serviceResponse = new ServiceResponse<>();
        
        try {
            // Validate token format
            UUID userToken = UUID.fromString(token);
            
            // Check if user exists in Redis
            String key = "user:" + token;
            Object user = redisTemplate.opsForValue().get(key);
            boolean loggedIn = user != null;
            
            serviceResponse.setData(loggedIn);
            serviceResponse.setOk(true);
            return serviceResponse;
            
        } catch (IllegalArgumentException e) {
            serviceResponse.setData(false);
            serviceResponse.setOk(false);
            serviceResponse.addError("token", "Invalid token format");
            return serviceResponse;
        } catch (Exception e) {
            serviceResponse.setData(false);
            serviceResponse.setOk(false);
            log.error("Error checking login status:", e);
            return serviceResponse;
        }
    }

    @BrokerOperation("getUserRegistrationForToken")
    public ServiceResponse<UserRegistrationDTO> getUserRegistrationForToken(@BrokerParam("token") String token) {
        log.debug("Retrieving user for token {}", token);
        
        ServiceResponse<UserRegistrationDTO> serviceResponse = new ServiceResponse<>();
        
        try {
            // Validate token format
            UUID userToken = UUID.fromString(token);
            
            // Get user from Redis
            String key = "user:" + token;
            Object userObj = redisTemplate.opsForValue().get(key);
            
            if (userObj instanceof UserRegistrationDTO) {
                UserRegistrationDTO user = (UserRegistrationDTO) userObj;
                serviceResponse.setData(user);
                serviceResponse.setOk(true);
            } else {
                serviceResponse.setData(null);
                serviceResponse.setOk(false);
                serviceResponse.addError("token", "Token not found or expired");
            }
            return serviceResponse;
            
        } catch (IllegalArgumentException e) {
            serviceResponse.setData(null);
            serviceResponse.setOk(false);
            serviceResponse.addError("token", "Invalid token format");
            return serviceResponse;
        } catch (Exception e) {
            serviceResponse.setData(null);
            serviceResponse.setOk(false);
            log.error("Error retrieving user for token:", e);
            return serviceResponse;
        }
    }

    /**
     * Get the logged-in user by token
     * @param token the UUID token
     * @return the UserRegistrationDTO if found, null otherwise
     */
    public UserRegistrationDTO getLoggedInUser(UUID token) {
        String key = "user:" + token.toString();
        Object userObj = redisTemplate.opsForValue().get(key);
        return userObj instanceof UserRegistrationDTO ? (UserRegistrationDTO) userObj : null;
    }

    /**
     * Get all currently logged-in users (this is a limitation with Redis; would require a Redis KEYS operation)
     * @return map of tokens to users (may be limited to current instance data)
     */
    public Map<UUID, UserRegistrationDTO> getLoggedInUsers() {
        log.warn("Getting all logged-in users from Redis is inefficient and may not return all users across instances. Consider redesigning to avoid this operation.");
        // For scalability, avoid KEYS operation in production
        // Instead, return empty map as Redis implementation doesn't support this efficiently
        return new java.util.HashMap<>();
    }

}
