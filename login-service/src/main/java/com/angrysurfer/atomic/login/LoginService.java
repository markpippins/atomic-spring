package com.angrysurfer.atomic.login;

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
            UserRegistrationDTO user = userAccessService.login(alias, password);

            if (user == null) {
                LoginResponse response = new LoginResponse("FAILURE", "invalid password");
                response.setOk(false);
                response.addError("identifier", "invalid password");

                serviceResponse.setOk(false);
                serviceResponse.setData(response);
                return serviceResponse;
            }

            LoginResponse response = new LoginResponse("SUCCESS", null);
            
            response.setUserId(user.getId());
            response.setAvatarUrl(user.getAvatarUrl());
            response.setOk(true);

            serviceResponse.setData(response);
            serviceResponse.setOk(true);
            return serviceResponse;

        } catch (Exception e) {
            serviceResponse.setData(new LoginResponse("FAILURE", e.getMessage()));
            serviceResponse.setOk(false);
            return serviceResponse;
        }

    }

}
