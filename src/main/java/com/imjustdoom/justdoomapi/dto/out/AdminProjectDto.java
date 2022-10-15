package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminProjectDto {

    public static AdminProjectDto create(String title, String blurb, String description, boolean isPublic, int id) {
        return new AdminProjectDto(title, blurb, description, isPublic, id);
    }

    private String title, blurb, description;
    private boolean isPublic;
    private int id;
}
