package com.imjustdoom.justdoomapi.dto.out;

import com.imjustdoom.justdoomapi.model.Update;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProjectDto {

    public static ProjectDto create(String title, String blurb, String description, List<Update> updates, int id) {
        return new ProjectDto(title, blurb, description, updates.stream().map(blog -> UpdateDto.create(blog.getUploaded(), blog.getDescription(), blog.getTitle(), blog.getFilename(), blog.getVersions(), blog.getSoftware(), blog.getVersion(), blog.getDownloads(), blog.getStatus(), blog.getId())).collect(Collectors.toList()), id);
    }

    private String title, blurb, description;
    private List<UpdateDto> updates;
    private int id;
}
