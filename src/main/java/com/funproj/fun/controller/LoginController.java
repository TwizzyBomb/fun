package com.funproj.fun.controller;

import com.funproj.fun.model.User;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Controller handling authentication-related view endpoints.
 *
 * <p>This controller manages:
 * <ul>
 *   <li>Login page rendering</li>
 *   <li>User registration form display</li>
 * </ul>
 *
 * <p><b>Security Note:</b> CSRF protection is automatically handled by Spring Security
 * when using Thymeleaf templates with form submissions.
 */
@Controller
public class LoginController {

    /**
     * Displays the login page.
     *
     * <p><b>Implementation Notes:</b>
     * <ul>
     *   <li>Returns the logical view name for Thymeleaf template resolution</li>
     *   <li>CSRF token is automatically included in forms by Thymeleaf</li>
     *   <li>No explicit model attributes needed for basic login form</li>
     * </ul>
     *
     * @return String representing the Thymeleaf template name ("login.html")
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }

    /**
     * Displays the user registration form.
     *
     * <p>Prepares a new User instance as a form-backing object for the registration form.
     *
     * <p><b>Template Requirements:</b>
     * <ul>
     *   <li>Expects a Thymeleaf template at "templates/register.html"</li>
     *   <li>Template should bind to the "user" model attribute</li>
     * </ul>
     *
     * @param model the model to which attributes are added
     * @return String representing the Thymeleaf template name ("register.html")
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // points to register.html Thymeleaf teamplate
    }
}