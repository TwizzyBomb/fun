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

/**
 * Service layer handling authentication and user registration logic.
 * Uses reactive programming model for non-blocking operations.
 */
@Service
public class AuthService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the AuthService with required dependencies.
     *
     * @param reactiveAuthenticationManager Handles reactive authentication flows
     * @param passwordEncoder Encodes passwords securely
     * @param userRepository DAO for user data operations
     * @param jwtUtil Utility for JWT token generation
     */
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

    /**
     * Authenticates a user and generates a JWT token upon successful authentication.
     *
     * @param username The username to authenticate
     * @param password The raw password to validate
     * @return Mono<String> containing the JWT token on success, or an error message on failure
     *
     * @implNote Flow:
     * 1. Creates authentication token with credentials
     * 2. Delegates to ReactiveAuthenticationManager
     * 3. On success: generates JWT token
     * 4. On failure: returns error message
     */
    public Mono<String> authenticate(String username, String password){
        String hashedPassword = passwordEncoder.encode(password);
        System.out.println("Hashed password in manager: " + hashedPassword);

        return reactiveAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password) // creates the token for passing into authManager.authenticate()
            ).doOnNext(auth -> System.out.println("Authenticated: " + auth.isAuthenticated())).doOnError(err -> System.err.println("Auth failed: " + err.getMessage()))
            .flatMap(authentication -> {
                // Ensure jwtUtil.generateToken() returns Mono<String> with the actual token
                return jwtUtil.generateToken(username)
                                .flatMap(token -> {
                                    System.out.println("JWToken:" + token );
                                    return Mono.just(token);
                                });
            })
            .onErrorResume(AuthenticationException.class, e-> {
                // handle authentication failure
                return Mono.just("Invalid username and or password");
                //return Mono.error(new RuntimeException("Invalid username or password", e));
            });
    }

    /**
     * Registers a new user with encrypted password.
     *
     * @param user The user to register (contains raw password)
     * @return Mono<User> containing the saved user with encrypted password
     *
     * @implNote Flow:
     * 1. Encodes the raw password using BCrypt
     * 2. Persists the user to the database
     */
    public Mono<User> register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
