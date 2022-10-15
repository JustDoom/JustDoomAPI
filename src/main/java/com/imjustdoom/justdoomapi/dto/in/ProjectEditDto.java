package com.imjustdoom.justdoomapi.dto.in;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectEditDto {

    private int id;
    private String title, description, blurb;
    private boolean isPublic;
}
