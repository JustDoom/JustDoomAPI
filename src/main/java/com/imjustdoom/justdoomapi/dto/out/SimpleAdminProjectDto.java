package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleAdminProjectDto {

    public static SimpleAdminProjectDto create(String title, String blurb, boolean isPublic, int id) {
        return new SimpleAdminProjectDto(title, blurb, isPublic, id);
    }

    private String title, blurb;
    private boolean isPublic;
    private int id;

}
