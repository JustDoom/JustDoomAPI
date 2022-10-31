package com.imjustdoom.justdoomapi.dto.in;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class ProjectCreateUpdateDto {

    private String title, description, filename, version, status;
    private List<String> versions, software;
}
