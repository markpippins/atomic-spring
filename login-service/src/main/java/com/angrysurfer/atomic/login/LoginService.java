package com.angrysurfer.atomic.login;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.user.UserDTO;
import com.angrysurfer.atomic.user.UserRegistrationDTO;
import com.angrysurfer.atomic.user.model.UserRegistration;
import com.angrysurfer.atomic.user.service.UserAccessService;

@Service("loginService")
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final UserAccessService userAccessService;
    private final Map<UUID, UserRegistrationDTO> loggedInUsers = new ConcurrentHashMap<>();

    public LoginService(UserAccessService userAccessService) {
        this.userAccessService = userAccessService;
        log.info("LoginService initialized");
    }

    @BrokerOperation("login")
    public ServiceResponse<LoginResponse> login(@BrokerParam("alias") String alias,
            @BrokerParam("identifier") String password) {

        log.info("Login user {}", alias);

        ServiceResponse<LoginResponse> serviceResponse = new ServiceResponse<>();

        try {
            UserRegistrationDTO user = userAccessService.validateUser(alias, password);

            if (user == null) {
                LoginResponse response = new LoginResponse("FAILURE", "invalid password");
                response.setOk(false);
                response.addError("identifier", "invalid password");

                serviceResponse.setOk(false);
                serviceResponse.setData(response);
                return serviceResponse;
            }

            // Generate UUID token for successful login
            UUID token = UUID.randomUUID();
            
            LoginResponse response = new LoginResponse("SUCCESS", null);
            response.setToken(token.toString());
            response.setUserId(user.getId());
            response.setAvatarUrl(user.getAvatarUrl());
            response.setOk(true);
            
            // Store user in logged-in users map
            loggedInUsers.put(token, user);

            serviceResponse.setData(response);
            serviceResponse.setOk(true);
            return serviceResponse;

        } catch (Exception e) {
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
            UUID userToken = UUID.fromString(token);
            UserRegistrationDTO removedUser = loggedInUsers.remove(userToken);
            
            boolean logoutSuccess = removedUser != null;
            serviceResponse.setData(logoutSuccess);
            serviceResponse.setOk(true);
            
            log.info("Logout {} for token {}", logoutSuccess ? "successful" : "failed (token not found)", token);
            return serviceResponse;
            
        } catch (IllegalArgumentException e) {
            serviceResponse.setData(false);
            serviceResponse.setOk(false);
            serviceResponse.addError("token", "Invalid token format");
            return serviceResponse;
        } catch (Exception e) {
            serviceResponse.setData(false);
            serviceResponse.setOk(false);
            return serviceResponse;
        }
    }

    @BrokerOperation("isLoggedIn")
    public ServiceResponse<Boolean> isLoggedIn(@BrokerParam("token") String token) {
        log.debug("Checking login status for token {}", token);
        
        ServiceResponse<Boolean> serviceResponse = new ServiceResponse<>();
        
        try {
            UUID userToken = UUID.fromString(token);
            boolean loggedIn = loggedInUsers.containsKey(userToken);
            
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
            return serviceResponse;
        }
    }

    @BrokerOperation("getUserByToken")
    public ServiceResponse<UserRegistrationDTO> getUserByToken(@BrokerParam("token") String token) {
        log.debug("Retrieving user for token {}", token);
        
        ServiceResponse<UserRegistrationDTO> serviceResponse = new ServiceResponse<>();
        
        try {
            UUID userToken = UUID.fromString(token);
            UserRegistrationDTO user = loggedInUsers.get(userToken);
            
            if (user != null) {
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
            return serviceResponse;
        }
    }

    /**
     * Get the logged-in user by token
     * @param token the UUID token
     * @return the UserRegistrationDTO if found, null otherwise
     */
    public UserRegistrationDTO getLoggedInUser(UUID token) {
        return loggedInUsers.get(token);
    }

    /**
     * Get all currently logged-in users
     * @return map of tokens to users
     */
    public Map<UUID, UserRegistrationDTO> getLoggedInUsers() {
        return new ConcurrentHashMap<>(loggedInUsers);
    }

}
