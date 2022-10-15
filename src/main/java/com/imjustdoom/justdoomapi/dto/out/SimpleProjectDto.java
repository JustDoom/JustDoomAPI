package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleProjectDto {

    public static SimpleProjectDto create(String title, String blurb, int id) {
        return new SimpleProjectDto(title, blurb, id);
    }

    private String title, blurb;
    private int id;

}
