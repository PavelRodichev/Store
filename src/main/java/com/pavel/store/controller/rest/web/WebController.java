package com.pavel.store.controller.rest.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("swaggerUrl", "/swagger-ui.html");
        return "home";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверный email или пароль!");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы!");
        }
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard") // ← ДОБАВЬТЕ ЭТОТ МЕТОД
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/swagger")
    public String swaggerRedirect() {
        return "redirect:/swagger-ui.html";
    }
}
