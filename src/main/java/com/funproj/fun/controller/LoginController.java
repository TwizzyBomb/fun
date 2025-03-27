package com.funproj.fun.controller;

import com.funproj.fun.model.User;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class LoginController {

    // Spring security in WebFlux handles CSRF tokens reactively and needs an explicit way to fetch the token
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // points to register.html Thymeleaf teamplate
    }
}

// CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
//        model.addAttribute("_csrf", csrfToken);
//        return "login"; // login.html from templates

//             return exchange.getAttribute(CsrfToken.class.getName())
//                    .cast(CsrfToken.class)
//                    .doOnNext(csrfToken -> mod.addAttribute("_csrf", csrfToken))
//                    .thenReturn("login");

//             return Mono.defer(() -> {
//                Mono<CsrfToken> csrfTokenMono = exchange.getAttribute(CsrfToken.class.getName());
//                return csrfTokenMono
//                        .doOnNext(csrfToken -> model.addAttribute("_csrf", csrfToken))
//                        .thenReturn("login");
//            });