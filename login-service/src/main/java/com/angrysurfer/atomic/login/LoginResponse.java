package com.angrysurfer.atomic.login;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class LoginResponse {

    private String userId;

    private String avatarUrl;

    private String token;

    private String message;

    private boolean ok = false;

    private Map<String, String> errors = new HashMap<>();

    private LoginResponse() {
    }
    
    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, String message) {
        this(token);
        this.message = message;
    }

    public void addError(String field, String message) {
        errors.put(field, message);
    }

    public void clearErrors() {
        errors.clear();
    }
}