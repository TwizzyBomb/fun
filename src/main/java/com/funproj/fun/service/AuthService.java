package com.funproj.fun.service;

import com.funproj.fun.model.User;
import com.funproj.fun.repository.UserRepository;
import com.funproj.fun.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(ReactiveAuthenticationManager reactiveAuthenticationManager,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       JwtUtil jwtUtil){
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public Mono<String> authenticate(String username, String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        System.out.println("Hashed password in manager: " + hashedPassword);
        return reactiveAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password) // creates the token for passing into authManager.authenticate()
            ).doOnNext(auth -> System.out.println("Authenticated: " + auth.isAuthenticated())).doOnError(err -> System.err.println("Auth failed: " + err.getMessage()))
            .flatMap(authentication -> {
                // if authentication is successful, generate a JWT token
                return jwtUtil.generateToken(username);
            })
            .onErrorResume(AuthenticationException.class, e-> {
                // handle authentication failure
                return Mono.just("Invalid username and or password");
                //return Mono.error(new RuntimeException("Invalid username or password", e));
            });
    }

    public Mono<User> register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
