package com.angrysurfer.atomic.vaadin.service;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.user.UserDTO;
import com.vaadin.flow.server.VaadinSession;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceClient {

    private BrokerClient brokerClient;
    private static final String SESSION_USER_KEY = "currentUser";
    private static final String SESSION_LOGGED_IN_KEY = "isLoggedIn";

    public UserServiceClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
        // log.info("UserServiceClient initialized");
    }

    public UserDTO login(String alias, String password) throws Exception {
        log.info("Logging in user with alias: {}", alias);
        Map<String, Object> params = Map.of(
                "alias", alias,
                "password", password
        );

        ServiceResponse<UserDTO> response = brokerClient.submitRequest(
                "userService",
                "login",
                params,
                new ParameterizedTypeReference<ServiceResponse<UserDTO>>() {
        }
        );

        if (!response.isOk() || response.getData() == null) {
            String errorMessage = response.getErrors() != null && !response.getErrors().isEmpty()
                    ? response.getErrors().get(0).get("message").toString()
                    : "Login failed";
            throw new Exception(errorMessage);
        }

        UserDTO user = response.getData();
        setCurrentUser(user);
        return user;
    }

    public UserDTO createUser(String alias, String name, String email) throws Exception {
        log.info("Creating user with alias: {}, name: {}, email: {}", alias, name, email);
        Map<String, Object> userParams = Map.of(
                "alias", alias,
                "name", name,
                "email", email
        );

        Map<String, Object> params = Map.of("user", userParams);

        ServiceResponse<UserDTO> response = brokerClient.submitRequest(
                "userService",
                "create",
                params,
                new ParameterizedTypeReference<ServiceResponse<UserDTO>>() {
        }
        );

        if (!response.isOk() || response.getData() == null) {
            String errorMessage = response.getErrors() != null && !response.getErrors().isEmpty()
                    ? response.getErrors().get(0).get("message").toString()
                    : "User creation failed";
            throw new Exception(errorMessage);
        }

        return response.getData();
    }

    public UserDTO getUserByAlias(String alias) throws Exception {
        log.info("Getting user by alias: {}", alias);
        Map<String, Object> params = Map.of("alias", alias);

        ServiceResponse<UserDTO> response = brokerClient.submitRequest(
                "userService",
                "getUserByAlias",
                params,
                new ParameterizedTypeReference<ServiceResponse<UserDTO>>() {
        }
        );

        if (!response.isOk() || response.getData() == null) {
            String errorMessage = response.getErrors() != null && !response.getErrors().isEmpty()
                    ? response.getErrors().get(0).get("message").toString()
                    : "User not found";
            throw new Exception(errorMessage);
        }

        return response.getData();
    }

    public void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(SESSION_USER_KEY, null);
            session.setAttribute(SESSION_LOGGED_IN_KEY, false);
        }
    }

    public boolean isLoggedIn() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return false;
        }

        Boolean loggedIn = (Boolean) session.getAttribute(SESSION_LOGGED_IN_KEY);
        return Boolean.TRUE.equals(loggedIn) && getCurrentUser() != null;
    }

    public UserDTO getCurrentUser() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return null;
        }

        return (UserDTO) session.getAttribute(SESSION_USER_KEY);
    }

    private void setCurrentUser(UserDTO user) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(SESSION_USER_KEY, user);
            session.setAttribute(SESSION_LOGGED_IN_KEY, true);
        }
    }
}
