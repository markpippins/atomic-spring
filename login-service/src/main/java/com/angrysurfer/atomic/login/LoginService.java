package com.angrysurfer.atomic.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.user.UserDTO;
import com.angrysurfer.atomic.user.service.UserService;

@Service("loginService")
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final UserService userService;

    public LoginService(UserService userService) {
        this.userService = userService;
        log.info("LoginService initialized");
    }

    @BrokerOperation("login")
    public ServiceResponse<LoginResponse> login(@BrokerParam("alias") String alias,
            @BrokerParam("identifier") String password) {

        log.info("Login user {}", alias);

        ServiceResponse<LoginResponse> serviceResponse = new ServiceResponse<>();

        try {
            UserDTO user = userService.findByAlias(alias);

            if (user == null || !user.getIdentifier().equals(password)) {

                LoginResponse response = new LoginResponse("FAILURE", "invalid password");
                response.setOk(false);
                response.addError("identifier", "invalid password");

                serviceResponse.setOk(false);
                serviceResponse.setData(response);
                return serviceResponse;
            }

            LoginResponse response = new LoginResponse("SUCCESS", null);
            response.setUser(user);
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
