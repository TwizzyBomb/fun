package com.funproj.fun.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable()) // Correct way to disable CSRF in Spring Security 6 AbstractHttpConfigurer::disable
                    .authorizeExchange(auth -> auth
                            .pathMatchers("/", "/login", "/css/**", "/js/**").permitAll() // Allow anyone to access these pages
                            .anyExchange().authenticated() // Require authentication for any other request
                    )
                    .formLogin(form -> form
                            .loginPage("/login") // custom login page
                            .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/home")) // Default success URL
                            .authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/login?error")) // Redirect on failure
                    )
//                    .logout(logout -> logout
//                            .logoutUrl("/logout") // Logout endpoint
//                            .logoutSuccessHandler(new RedirectServerLogoutSuccessHandler()
//                                    .setLogoutSuccessUrl( URI.create("/login?logout")) )  // Logout success URL
//                    )
                    .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }

    // Define the in-memory user details
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password")) // Securely hash password
                .roles("USER")
                .build();

        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // use BCrypt for secure password hashing
    }
}
