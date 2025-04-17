package com.funproj.fun.controller;

import com.funproj.fun.model.LoginRequest;
import com.funproj.fun.model.User;
import com.funproj.fun.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

/**
 * Controller handling authentication-related operations including user registration and login.
 * Uses reactive programming model for non-blocking operations.
 */
@Controller
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private ReactiveAuthenticationManager reactiveAuthenticationManager;
    private PasswordEncoder passwordEncoder;

    /**
     * Constructs an AuthController with required dependencies.
     *
     * @param authService Authentication service for business logic
     * @param reactiveAuthenticationManager Reactive authentication manager
     * @param passwordEncoder Password encoder for secure password handling
     */
    @Autowired
    public AuthController(
            AuthService authService,
            ReactiveAuthenticationManager reactiveAuthenticationManager,
            PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Handles user registration requests.
     *
     * @param user User object containing registration details
     * @return Mono<String> that redirects to login page after successful registration
     */
    @PostMapping("/register")
    public Mono<String> register(@ModelAttribute User user) {

        return authService.register(user)
                .doOnSuccess(savedUser -> System.out.println("User saved: " + savedUser.getUsername()))
                .flatMap(savedUser -> Mono.just("redirect:/auth/login")); // Redirect to login page after successful registration
    }

    /**
     * Handles user login requests.
     *
     * @param requestMono Mono containing the login request with username and password
     * @param exchange ServerWebExchange for accessing and modifying the HTTP response
     * @return Mono<Void> that completes when the response is handled
     *
     * @apiNote Successful authentication:
     *          - Sets a secure HTTP-only cookie with JWT token
     *          - Redirects to "/home" with HTTP 303 (SEE_OTHER)
     *          Failed authentication:
     *          - Returns HTTP 401 (UNAUTHORIZED)
     */
    @PostMapping("/login")
    public Mono<Void> login(@RequestBody Mono<LoginRequest> requestMono, ServerWebExchange exchange) {
        return requestMono.flatMap(request -> {
            System.out.println("Login Request received for:" + request.getUsername());
            return authService.authenticate(request.getUsername(), request.getPassword())
                    .flatMap(token -> {
                        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(Duration.ofDays(1))
                                .sameSite("Strict")
                                .build();

                        exchange.getResponse().addCookie(cookie);
                        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                        exchange.getResponse().getHeaders().setLocation(URI.create("/home"));
                        return exchange.getResponse().setComplete();
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }));
        });
    }
}
