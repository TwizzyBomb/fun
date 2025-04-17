package com.funproj.fun.model;

/**
 * Represents a login request containing user credentials.
 *
 * <p>This DTO (Data Transfer Object) separates authentication concerns from the {@code User} model
 * and is used specifically for handling login requests in the application.
 *
 * <p><b>Security Note:</b> Instances of this class should:
 * <ul>
 *   <li>Only be used for the initial authentication request</li>
 *   <li>Never be persisted to long-term storage</li>
 *   <li>Have passwords cleared from memory after authentication completes</li>
 * </ul>
 */
public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest() {};

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
