package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleProjectDto {

    public static SimpleProjectDto create(String title, String blurb, String slug, long updated, int id) {
        return new SimpleProjectDto(title, blurb, slug, updated, id);
    }

    private String title, blurb, slug;
    private long updated;
    private int id;

}
