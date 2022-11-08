package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectNameDto {

    public static ProjectNameDto create(String title) {
        return new ProjectNameDto(title);
    }

    private String title;
}
