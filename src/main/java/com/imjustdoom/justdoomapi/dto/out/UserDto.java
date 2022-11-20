package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

    public static UserDto create(String username, String role, long joined, int id) {
        return new UserDto(username, role, joined, id);
    }

    private String username, role;
    private long joined;
    private int id;
}
