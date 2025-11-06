package com.pavel.store.controller.rest.web;


import com.pavel.store.entity.User;
import com.pavel.store.repository.UserRepository;
import com.pavel.store.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class WebAuthController {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;

    @PostMapping("/web/login")
    public String authenticateUserForm(@RequestParam String username,
                                       @RequestParam String password,
                                       HttpServletResponse response) {
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            // Проверим существование пользователя
            boolean userExists = userRepository.existsByEmail(username);
            System.out.println("User exists: " + userExists);

            if (userExists) {
                User user = userRepository.findByEmail(username).orElse(null);
                System.out.println("User found: " + (user != null ? user.getEmail() : "null"));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            System.out.println("JWT Token generated: " + jwt);

            Cookie jwtCookie = new Cookie("JWT", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(jwtCookie);

            System.out.println("Login successful!");
            return "redirect:/dashboard";

        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/login?error=true";
        }
    }
}