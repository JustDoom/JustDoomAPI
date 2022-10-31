package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

    public static UserDto create(String username, String role, int id) {
        return new UserDto(username, role, id);
    }

    private String user, role;
    private int id;
}
