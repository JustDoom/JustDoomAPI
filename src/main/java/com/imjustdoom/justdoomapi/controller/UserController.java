package com.imjustdoom.justdoomapi.controller;

import com.imjustdoom.justdoomapi.dto.in.UserFromTokenDto;
import com.imjustdoom.justdoomapi.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final AccountService accountService;

    @PostMapping("/auth/user")
    public ResponseEntity<?> getUser(@RequestBody UserFromTokenDto dto) {
        return accountService.getUserByToken(dto.getToken());
    }
}
