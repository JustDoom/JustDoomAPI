package com.imjustdoom.justdoomapi.controller;

import com.google.gson.Gson;
import com.imjustdoom.justdoomapi.config.APIConfig;
import com.imjustdoom.justdoomapi.dto.in.ProjectCreateUpdateDto;
import com.imjustdoom.justdoomapi.dto.in.ProjectCreationDto;
import com.imjustdoom.justdoomapi.dto.in.ProjectEditDto;
import com.imjustdoom.justdoomapi.dto.out.AdminProjectDto;
import com.imjustdoom.justdoomapi.dto.out.ProjectDto;
import com.imjustdoom.justdoomapi.dto.out.SimpleAdminProjectDto;
import com.imjustdoom.justdoomapi.dto.out.SimpleProjectDto;
import com.imjustdoom.justdoomapi.model.Account;
import com.imjustdoom.justdoomapi.model.Project;
import com.imjustdoom.justdoomapi.model.Update;
import com.imjustdoom.justdoomapi.repository.ProjectRepository;
import com.imjustdoom.justdoomapi.repository.TokenRepository;
import com.imjustdoom.justdoomapi.repository.UpdateRepository;
import com.imjustdoom.justdoomapi.service.AuthService;
import com.imjustdoom.justdoomapi.service.FileService;
import com.imjustdoom.justdoomapi.util.APIUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
public class ResourceApiController {

    private final ProjectRepository projectRepository;
    private final UpdateRepository updateRepository;
    private final TokenRepository tokenRepository;

    private final AuthService authService;
    private final FileService fileService;

    private final APIConfig config;

    //
    //  Projects
    //

    @GetMapping
    public ResponseEntity<?> projects() {
        List<SimpleProjectDto> projectDtos = projectRepository.findAllByIsPublic(true).stream().map(blog -> {
            Optional<Update> latestUpdate = updateRepository.findFirstByProjectIdOrderByUploadedDesc(blog.getId());
            return SimpleProjectDto.create(blog.getTitle(), blog.getBlurb(), latestUpdate.isEmpty() ? -1 : latestUpdate.get().getUploaded(), blog.getId());
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(projectDtos);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> project(@PathVariable("id") int id) {

        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        return ResponseEntity.ok().body(ProjectDto.create(project.get().getTitle(), project.get().getBlurb(), project.get().getDescription(), project.get().getCreated(), updateRepository.findAllByProjectId(project.get().getId()), project.get().getId()));
    }

    @GetMapping("{id}/updates/{updateId}/download")
    public ResponseEntity<?> downloadUpdate(@PathVariable("id") int id, @PathVariable("updateId") int updateId) {

        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        Optional<Update> update = updateRepository.findById(updateId);
        if (update.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Update not found"));
        }

        String link = this.fileService.getDownload(updateId);
        if (link == null) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("File not found"));
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(link)).build();
    }

    //
    //  Admin area
    //

    @GetMapping("admin/projects")
    public ResponseEntity<?> projectsAdmin(@RequestHeader("authorization") String token) {

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!authService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        List<SimpleAdminProjectDto> projectDtos = projectRepository.findAll().stream().map(blog -> SimpleAdminProjectDto.create(blog.getTitle(), blog.getBlurb(), blog.isPublic(), blog.getId())).collect(Collectors.toList());
        return ResponseEntity.ok().body(projectDtos);
    }

    @GetMapping("admin/projects/{id}")
    public ResponseEntity<?> projectAdmin(@RequestHeader("authorization") String token, @PathVariable("id") int id) {

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!authService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        return ResponseEntity.ok().body(AdminProjectDto.create(project.get().getTitle(), project.get().getBlurb(), project.get().getDescription(), project.get().isPublic(), project.get().getId()));
    }

    @PostMapping("create")
    public ResponseEntity<?> createProject(@RequestHeader("authorization") String token, @RequestBody ProjectCreationDto dto) {

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!authService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        Project blogPost = new Project(dto.getTitle(), dto.getDescription(), dto.getBlurb(), dto.isPublic(), account);
        projectRepository.save(blogPost);

        return ResponseEntity.ok().body(APIUtil.createSuccessResponse("Created a new project!"));
    }

    @PostMapping("{id}/update")
    public ResponseEntity<?> createProjectUpdate(@PathVariable("id") int id, @RequestHeader("authorization") String token, @RequestParam("file") MultipartFile file, @RequestPart("data") String data) {

        ProjectCreateUpdateDto dto = new Gson().fromJson(data, ProjectCreateUpdateDto.class);

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!authService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        Update update = new Update(dto.getTitle(), dto.getDescription(), file.getOriginalFilename(), dto.getVersions(), dto.getSoftware(), dto.getVersion(), dto.getStatus(), project.get());
        updateRepository.save(update);

        try (InputStream inputStream = file.getInputStream()) {

            System.out.println("Saving file " + file.getOriginalFilename());
            // PUT request to upload file to server with headers
            HttpURLConnection connection = (HttpURLConnection) new URL(config.getStorageUrl() + "/" + update.getId() + "/" + file.getOriginalFilename()).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("X-Custom-Auth-Key", config.getStorageSecret());
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(100000);
            connection.setReadTimeout(100000);
            connection.connect();
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            connection.getInputStream();
            connection.disconnect();
            System.out.println("Uploaded file to server");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(APIUtil.createSuccessResponse("Created a new project!"));
    }

    @PostMapping("{id}/edit")
    public ResponseEntity<?> editProject(@RequestHeader("authorization") String token, @RequestBody ProjectEditDto dto) {
        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!authService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        projectRepository.updateProjectById(dto.getTitle(), dto.getDescription(), dto.getBlurb(), dto.isPublic(), dto.getId());

        return ResponseEntity.ok().body(APIUtil.createSuccessResponse("Edit the project!"));
    }


    // TODO: make this apply to admin pages too without a whole second method
    @Bean
    public WebMvcConfigurer corsConfigurerResourceAPI() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/projects/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST");
            }
        };
    }
}
