package com.funproj.fun.controller;

import com.funproj.fun.model.User;
import com.funproj.fun.service.AuthService;
import io.jsonwebtoken.security.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private ReactiveAuthenticationManager reactiveAuthenticationManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(
            AuthService authService,
            ReactiveAuthenticationManager reactiveAuthenticationManager,
            PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public Mono<String> register(@ModelAttribute User user) {

        return authService.register(user)
                .doOnSuccess(savedUser -> System.out.println("User saved: " + savedUser.getUsername()))
                .flatMap(savedUser -> Mono.just("redirect:/auth/login")); // Redirect to login page after successful registration
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody User user){
        System.out.println("Login Request received for:" + user.getUsername());
        return authService.authenticate(user.getUsername(), user.getPassword());
    }
}
