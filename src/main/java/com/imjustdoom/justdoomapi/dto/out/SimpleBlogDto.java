package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleBlogDto {

    public static SimpleBlogDto create(String title, String author, long created, int id) {
        return new SimpleBlogDto(title, author, id, created);
    }

    private String title, author;
    private int id;
    private long created;

}
