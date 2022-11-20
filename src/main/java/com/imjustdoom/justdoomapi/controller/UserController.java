package com.imjustdoom.justdoomapi.controller;

import com.imjustdoom.justdoomapi.dto.in.UserFromTokenDto;
import com.imjustdoom.justdoomapi.dto.out.SimpleUserDto;
import com.imjustdoom.justdoomapi.dto.out.UserDto;
import com.imjustdoom.justdoomapi.repository.AccountRepository;
import com.imjustdoom.justdoomapi.service.AuthService;
import com.imjustdoom.justdoomapi.util.APIUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.ZoneOffset;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final AccountRepository accountRepository;

    private final AuthService authService;

    @PostMapping("/user")
    public ResponseEntity<?> getUser(@RequestBody UserFromTokenDto dto) {
        return authService.getUserByToken(dto.getToken());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader("authorization") String token) {

        if(!authService.isAdmin(token)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        return ResponseEntity.ok().body(accountRepository.findAll().stream().map(user -> SimpleUserDto.create(user.getUsername(), user.getRole(), user.getJoined().toEpochSecond(ZoneOffset.ofHours(10)), user.getId())).collect(Collectors.toList()));
        //return accountRepository.findAll().isEmpty() ? ResponseEntity.ok().body("No users found") : ResponseEntity.ok().body(accountRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserInfo(@RequestHeader("authorization") String token, @PathVariable int id) {

        if(!authService.isAdmin(token)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        return ResponseEntity.ok().body(accountRepository.findById(id).map(user -> UserDto.create(user.getUsername(), user.getRole(), user.getJoined().toEpochSecond(ZoneOffset.ofHours(10)), user.getId())).orElse(null));

        //return accountRepository.findAll().isEmpty() ? ResponseEntity.ok().body("No users found") : ResponseEntity.ok().body(accountRepository.findAll());
    }

    @Bean
    public WebMvcConfigurer corsConfigurerUsers() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/user")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST");
            }
        };
    }
}