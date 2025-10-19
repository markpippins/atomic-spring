package com.angrysurfer.atomic.user.service;

import com.angrysurfer.atomic.user.UserDTO;
import com.angrysurfer.atomic.user.model.ValidUser;
import com.angrysurfer.atomic.user.repository.UserRepository;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("userAccessService")
public class UserAccessService {

    private static final Logger log = LoggerFactory.getLogger(UserAccessService.class);

    private final UserRepository userRepository;

    public UserAccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("UserAccessService initialized");
    }

    @BrokerOperation("login")
    public UserDTO login(@BrokerParam("alias") String alias, @BrokerParam("identifier") String password) {

        log.info("Login user {}", alias);
        ValidUser user = userRepository.findByAlias(alias).orElse(null);

        if (user == null || !user.getIdentifier().equals(password)) {
            return null;
        }

        return user.toDTO();
    }
}