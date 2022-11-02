package com.imjustdoom.justdoomapi.controller;

import com.imjustdoom.justdoomapi.config.APIConfig;
import com.imjustdoom.justdoomapi.dto.in.LoginDto;
import com.imjustdoom.justdoomapi.dto.in.RegisterDto;
import com.imjustdoom.justdoomapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final APIConfig config;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto data, @CookieValue(name = "token", required = false) String token) {
        return authService.login(data, token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto data, @CookieValue(name = "token", required = false) String token) {
        return authService.register(data, token);
    }

    @PostMapping("/logout2")
    public ResponseEntity<?> logout() {
        return authService.logout();
    }

    @Bean
    public WebMvcConfigurer corsConfigurerAuth() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/auth/**")
                        .allowedOrigins(config.getFrontendUrl())
                        .allowedOriginPatterns(config.getFrontendUrl())
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST");
            }
        };
    }
}