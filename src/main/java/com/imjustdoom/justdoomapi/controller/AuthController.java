package com.imjustdoom.justdoomapi.controller;

import com.imjustdoom.justdoomapi.dto.in.BlogPostDto;
import com.imjustdoom.justdoomapi.dto.in.LoginDto;
import com.imjustdoom.justdoomapi.dto.in.RegisterDto;
import com.imjustdoom.justdoomapi.model.Account;
import com.imjustdoom.justdoomapi.model.BlogPost;
import com.imjustdoom.justdoomapi.repository.AccountRepository;
import com.imjustdoom.justdoomapi.repository.BlogRepository;
import com.imjustdoom.justdoomapi.repository.ProjectRepository;
import com.imjustdoom.justdoomapi.repository.TokenRepository;
import com.imjustdoom.justdoomapi.service.AccountService;
import com.imjustdoom.justdoomapi.util.APIUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto data, @CookieValue(name = "token", required = false) String token) {
        return accountService.login(data, token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto data, @CookieValue(name = "token", required = false) String token) {
        return accountService.register(data, token);
    }

    @PostMapping("/logout2")
    public ResponseEntity<?> logout() {
        return accountService.logout();
    }

    @Bean
    public WebMvcConfigurer corsConfigurerAuth() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/auth/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedOriginPatterns("http://localhost:3000")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST");
            }
        };
    }
}