package com.imjustdoom.justdoomapi.controller;

import com.imjustdoom.justdoomapi.dto.in.UserFromTokenDto;
import com.imjustdoom.justdoomapi.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@AllArgsConstructor
public class UserController {

    private final AccountService accountService;

    @PostMapping("/auth/user")
    public ResponseEntity<?> getUser(@RequestBody UserFromTokenDto dto) {
        return accountService.getUserByToken(dto.getToken());
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurerUser() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/auth/**")
//                        .allowedOrigins("http://localhost:3000")
//                        .allowedOriginPatterns("http://localhost:3000")
//                        .allowCredentials(true)
//                        .allowedMethods("GET", "POST");
//            }
//        };
//    }
}
