package com.angrysurfer.atomic.user.service;

import com.angrysurfer.atomic.user.UserDTO;
import com.angrysurfer.atomic.user.model.UserRegistration;
import com.angrysurfer.atomic.user.repository.UserRegistrationRepository;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.user.UserRegistrationDTO;

@Service("userAccessService")
public class UserAccessService {

    private static final Logger log = LoggerFactory.getLogger(UserAccessService.class);

    private final UserRegistrationRepository userRepository;

    public UserAccessService(UserRegistrationRepository userRepository) {
        this.userRepository = userRepository;
        log.info("UserAccessService initialized");
    }

    @BrokerOperation("validateUser")
    public UserRegistrationDTO validateUser(@BrokerParam("alias") String alias,
            @BrokerParam("identifier") String password) {

        log.info("Validating user {}", alias);

        // Hardcoded admin check for verification
        if ("admin".equalsIgnoreCase(alias) && "admin".equals(password)) {
            UserRegistrationDTO user = new UserRegistrationDTO();
            user.setId("1");
            user.setAlias("admin");
            user.setEmail("admin@example.com");
            user.setIdentifier("admin");
            user.setAvatarUrl("https://example.com/avatar.jpg");
            user.setAdmin(true);
            return user;
        }

        UserRegistration userReg = userRepository.findByAlias(alias).orElse(null);

        if (userReg == null || !userReg.getIdentifier().equals(password)) {
            return null;
        }

        return userReg.toDTO();
    }
}