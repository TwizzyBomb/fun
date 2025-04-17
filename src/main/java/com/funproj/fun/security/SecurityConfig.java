package com.funproj.fun.security;

import com.funproj.fun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration class for the application.
 * Configures web security using Spring WebFlux Security including JWT authentication.
 *
 * <p>This class defines:
 * <ul>
 *   <li>Security filter chain with path-based access rules</li>
 *   <li>JWT authentication filter setup</li>
 *   <li>Authentication manager configuration</li>
 *   <li>User details service implementation</li>
 *   <li>Password encoder bean</li>
 * </ul>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    UserRepository userRepository;
    private final JwtAuthenticationWebFilter jwtFilter;

    /**
     * Constructs a new SecurityConfig with required dependencies.
     *
     * @param jwtFilter the JWT authentication filter to be used
     */
    public SecurityConfig(JwtAuthenticationWebFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http the ServerHttpSecurity to configure
     * @return the configured SecurityWebFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(auth -> auth
                        .pathMatchers("/", "/login", "/register", "/css/**", "/js/**", "/auth/**").permitAll() // public endpoints
                        .anyExchange().authenticated() // Require authentication for any other endpoint ( home )
                )
//                    .formLogin(form -> form // not used here, using custom login endpoint instead of form validation
//                            .loginPage("/login") // custom login page
//                            .authenticationSuccessHandler((webFilterExchange, authentication) -> {
//                                System.out.println("Authentication successful! Redirecting to /home");
//                                return new RedirectServerAuthenticationSuccessHandler("/home")
//                                        .onAuthenticationSuccess(webFilterExchange, authentication);
//                            })// Default success URL
//                            .authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/login?error")) // Redirect on failure
//                    )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION) // add JWT filter
//                    .logout(logout -> logout
//                            .logoutUrl("/logout") // Logout endpoint
//                            .logoutSuccessHandler(new RedirectServerLogoutSuccessHandler()
//                                    .setLogoutSuccessUrl( URI.create("/login?logout")) )  // Logout success URL
//                    )
                .build();
    }

    /**
     * Creates a reactive authentication manager with the provided user details service and password encoder.
     *
     * @param userDetailsService the reactive user details service
     * @param passwordEncoder the password encoder to use
     * @return configured ReactiveAuthenticationManager
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }

    /**
     * Creates a reactive user details service that fetches users from the repository.
     *
     * @param userRepository the user repository to fetch user details from
     * @return configured ReactiveUserDetailsService
     */
    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) { // added repo to args
        return username -> userRepository.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPassword()) // password must already be hashed in db
                        .roles("USER") // Modify based on your roles setup
                        .build()
                );
    }

    /**
     * Provides a password encoder bean using BCrypt hashing.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // use BCrypt for secure password hashing
    }

}