package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleProjectDto {

    public static SimpleProjectDto create(String title, String blurb, long updated, int id) {
        return new SimpleProjectDto(title, blurb, updated, id);
    }

    private String title, blurb;
    private long updated;
    private int id;

}
