package com.angrysurfer.atomic.note;

import com.angrysurfer.atomic.broker.Broker;
import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.user.UserRegistrationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NoteTokenService {

    private static final Logger log = LoggerFactory.getLogger(NoteTokenService.class);

    private final Broker broker;

    public NoteTokenService(Broker broker) {
        this.broker = broker;
        log.info("NoteTokenService initialized");
    }

    public String getUserIdFromToken(String token) {
        try {
            // Create a service request to the login service to get user registration by token
            ServiceRequest request = new ServiceRequest(
                "loginService", 
                "getUserRegistrationForToken", 
                Map.of("token", token), 
                "get-user-registration-" + System.currentTimeMillis()
            );

            // Submit the request via the broker
            @SuppressWarnings("unchecked")
            ServiceResponse<UserRegistrationDTO> response = (ServiceResponse<UserRegistrationDTO>) broker.submit(request);

            if (response.isOk() && response.getData() != null) {
                UserRegistrationDTO userRegistration = response.getData();
                log.debug("Retrieved user ID {} for token {}", userRegistration.getId(), token);
                return userRegistration.getId();
            } else {
                log.warn("Failed to retrieve user registration for token {}: {}", token, 
                    response.getErrors() != null ? response.getErrors() : "No data returned");
                return null;
            }
        } catch (Exception e) {
            log.error("Error retrieving user ID from token: ", e);
            return null;
        }
    }
}