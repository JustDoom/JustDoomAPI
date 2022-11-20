package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleUserDto {

    public static SimpleUserDto create(String username, String role, long joined, int id) {
        return new SimpleUserDto(username, role, joined, id);
    }

    private String username, role;
    private long joined;
    private int id;
}
