package com.angrysurfer.atomic.login;

import java.util.HashMap;
import java.util.Map;

import com.angrysurfer.atomic.user.UserDTO;

public class LoginResponse {

    private String token;

    private String message;

    private boolean ok = false;

    private UserDTO user;

    private Map<String, String> errors = new HashMap<>();

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, String message) {
        this(token);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void addError(String field, String message) {
        errors.put(field, message);
    }

    public void clearErrors() {
        errors.clear();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}