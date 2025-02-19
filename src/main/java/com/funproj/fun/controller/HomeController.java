package com.funproj.fun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("greeting", "Hello, ");
        model.addAttribute("name", "Adrian");
        return "home"; // Corresponds to logged_in.html in templates
    }
}
