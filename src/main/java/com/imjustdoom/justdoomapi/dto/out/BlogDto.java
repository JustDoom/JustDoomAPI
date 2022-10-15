package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlogDto {

    public static BlogDto create(String title, String post, String author, String created, int id) {
        return new BlogDto(title, author, post, id, created);
    }

    private String title, author, post;
    private int id;
    private String created;

}
