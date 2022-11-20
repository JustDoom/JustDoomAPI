package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MiddlewareUserDto {

    public static MiddlewareUserDto create(String username, String role, int id) {
        return new MiddlewareUserDto(username, role, id);
    }

    private String user, role;
    private int id;
}
