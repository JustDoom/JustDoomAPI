package com.imjustdoom.justdoomapi.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateDto {

    public static UpdateDto create(long uploaded, String description, String title, String filename, List<String> versions, List<String> software, String version, int downloads, String status, int id) {
        return new UpdateDto(uploaded, description, title, filename, version, status, versions, software, downloads, id);
    }

    private long uploaded;
    private String description, title, filename, version, status;
    private List<String> versions, software;
    private int downloads;
    private int id;
}
