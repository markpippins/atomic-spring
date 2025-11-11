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

    @BrokerOperation("login")
    public UserRegistrationDTO login(@BrokerParam("alias") String alias, @BrokerParam("identifier") String password) {

        log.info("Login user {}", alias);
        UserRegistration user = userRepository.findByAlias(alias).orElse(null);

        if (user == null || !user.getIdentifier().equals(password)) {
            return null;
        }

        return user.toDTO();
    }
}