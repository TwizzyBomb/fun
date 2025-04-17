package com.funproj.fun.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * Utility class for JWT (JSON Web Token) operations.
 * Provides methods for token generation, validation, and claim extraction.
 *
 * <p>This component handles:
 * <ul>
 *   <li>Token generation with expiration</li>
 *   <li>Token validation and verification</li>
 *   <li>Claim extraction from tokens</li>
 *   <li>Username extraction from tokens</li>
 * </ul>
 *
 * <p>Uses HMAC-SHA256 algorithm for signing and verification.
 */
@Component
public class JwtUtil {

    // Default secret key (should be overridden in production)
    private static final String SECRET_KEY = "2tvBIHDTGrffC9STSeN2xRP2SOo6n7qJZ2P5ufP5n+k=";
    // Default expiration time (1 hour in milliseconds)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    private final SecretKey secretKey;
    private final long expirationTime;

    /**
     * Constructs a JwtUtil instance with configurable secret and expiration.
     *
     * @param secret the secret key for signing tokens (injected from properties)
     * @param expirationTime token expiration time in milliseconds (injected from properties)
     */
    public JwtUtil(
            @Value("${jwt.secret:2tvBIHDTGrffC9STSeN2xRP2SOo6n7qJZ2P5ufP5n+k=}") String secret,
            @Value("${jwt.expiration:3600000}") long expirationTime) {
        this.secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        this.expirationTime = EXPIRATION_TIME;
    }

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the subject to include in the token
     * @return Mono containing the generated token string
     */
    public Mono<String> generateToken(String username) {
        return Mono.fromCallable(() ->
                Jwts.builder()
                        .subject(username)
                        .issuedAt(Date.from(Instant.now()))
                        .expiration(Date.from(Instant.now().plusMillis(expirationTime)))
                        .signWith(secretKey, Jwts.SIG.HS256)
                        .compact()
        );
    }

    /**
     * Extracts all claims from the given token.
     *
     * @param token the JWT token to parse
     * @return Mono containing the claims, or empty Mono if token is invalid
     */
    public Mono<Claims> extractAllClaims(String token) {
        return Mono.fromCallable(() ->
                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
        ).onErrorResume(JwtException.class, e -> Mono.empty());
    }

    /**
     * Validates whether a token is still valid (not expired).
     *
     * @param token the JWT token to validate
     * @return Mono<Boolean> true if token is valid, false otherwise
     */
    public Mono<Boolean> isTokenValid(String token) {
        return extractAllClaims(token)
                .map(claims -> claims.getExpiration().after(Date.from(Instant.now())))
                .defaultIfEmpty(false);
    }

    /**
     * Extracts the username from the given token.
     *
     * @param token the JWT token to parse
     * @return Mono containing the username, or empty Mono if token is invalid
     */
    public Mono<String> getUsernameFromToken(String token) {
        return extractAllClaims(token)
                .map(Claims::getSubject);
    }

    /**
     * Synchronous method to validate a token's signature and structure.
     * Primarily used by filter chains that require non-reactive validation.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}