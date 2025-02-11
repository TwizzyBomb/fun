package com.funproj.fun.controller;

import com.funproj.fun.model.User;
import com.funproj.fun.service.AuthService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Mono<User> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody User user){
        return authService.authenticate(user.getUsername(), user.getPassword());
    }
}
