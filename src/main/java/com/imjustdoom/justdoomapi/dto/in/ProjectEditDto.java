package com.imjustdoom.justdoomapi.dto.in;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectEditDto {

    private String slug;
    private String title, description, blurb;
    private boolean isPublic;
}
