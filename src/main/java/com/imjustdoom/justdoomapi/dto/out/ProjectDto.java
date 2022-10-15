package com.imjustdoom.justdoomapi.dto.out;

import com.imjustdoom.justdoomapi.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDto {

    public static ProjectDto create(String title, String blurb, String description, int id) {
        return new ProjectDto(title, blurb, description, id);
    }

    private String title, blurb, description;
    private int id;
}
