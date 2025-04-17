package com.funproj.fun.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling the main application home page and related views.
 *
 * <p>This controller manages post-authentication views and serves as the
 * primary entry point for authenticated users.
 */
@Controller
public class HomeController {

    /**
     * Displays the post-login home page with authenticated user details.
     *
     * @param model The model to populate with attributes
     * @param userDetails Automatically injected authenticated user details
     * @return Thymeleaf template name
     */
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = (userDetails != null) ? userDetails.getUsername() : "Guest";
        model.addAttribute("greeting", "Hello, ");
        model.addAttribute("name", username);
        return "home";
    }

}