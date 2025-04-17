package com.funproj.fun.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpCookie;
import com.funproj.fun.security.JwtUtil;

import java.util.Collections;

/**
 * JWT Authentication WebFilter that processes JWT tokens from cookies.
 *
 * <p>This filter:
 * <ul>
 *   <li>Intercepts incoming requests and checks for JWT cookie</li>
 *   <li>Validates the JWT token if present</li>
 *   <li>Sets up Spring Security authentication context for valid tokens</li>
 *   <li>Continues the filter chain regardless of token presence (stateless)</li>
 * </ul>
 */
@Component
public class JwtAuthenticationWebFilter implements WebFilter {
    private final JwtUtil jwtUtil;

    /**
     * Constructs the JWT authentication filter with required dependencies.
     *
     * @param jwtUtil Utility service for JWT token validation and processing
     */
    public JwtAuthenticationWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Processes each incoming request to handle JWT authentication.
     *
     * <p>The filter executes the following workflow:
     * <ol>
     *   <li>Checks if request should bypass authentication (public endpoints)</li>
     *   <li>Extracts JWT token from 'jwt' cookie if present</li>
     *   <li>Validates token using JwtUtil</li>
     *   <li>If valid, creates Authentication object and sets security context</li>
     *   <li>Continues filter chain with appropriate authentication state</li>
     * </ol>
     *
     * @param exchange Current server web exchange containing request/response
     * @param chain The web filter chain to continue processing
     * @return Mono<Void> indicating completion of request processing
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
        if (shouldSkipFilter(exchange.getRequest())){
            return chain.filter(exchange);
        }

        return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("jwt"))
                .map(HttpCookie::getValue)
                .filter(jwtUtil::validateToken)
                .map(jwtUtil::getUsernameFromToken)
                .flatMap(username -> {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.emptyList());
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    /**
     * Determines if a request should bypass JWT authentication.
     *
     * @param request The incoming HTTP request
     * @return true if the request path matches whitelisted endpoints, false otherwise
     */
    private boolean shouldSkipFilter(ServerHttpRequest request){
        String path = request.getPath().toString();
        return path.startsWith("/login") ||
                path.startsWith("/register") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/auth/");
    }
}
