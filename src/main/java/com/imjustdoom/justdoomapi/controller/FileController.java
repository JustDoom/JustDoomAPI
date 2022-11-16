package com.imjustdoom.justdoomapi.controller;

import com.imjustdoom.justdoomapi.model.Update;
import com.imjustdoom.justdoomapi.repository.ProjectRepository;
import com.imjustdoom.justdoomapi.repository.UpdateRepository;
import com.imjustdoom.justdoomapi.service.FileService;
import com.imjustdoom.justdoomapi.util.APIUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {

    private final ProjectRepository projectRepository;
    private final UpdateRepository updateRepository;

    private final FileService fileService;

    @GetMapping("{fileId}/download")
    public ResponseEntity<?> downloadUpdate(@PathVariable("fileId") int updateId) {

        Optional<Update> update = updateRepository.findById(updateId);
        if (update.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Update not found"));
        }

        // TODO: i think this is done weirdly, redo at some point
        String link = this.fileService.getDownload(updateId);
        if (link == null) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("File not found"));
        }

        // TODO: restrict files that are private
//        Optional<Project> project = projectRepository.findById(id);
//        if (project.isEmpty()) {
//            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
//        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(link)).build();
    }
}
