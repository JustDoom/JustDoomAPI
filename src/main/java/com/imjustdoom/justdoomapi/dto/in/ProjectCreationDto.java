package com.imjustdoom.justdoomapi.dto.in;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreationDto {

    private String title, slug, description, blurb;
    private boolean isPublic;
}
