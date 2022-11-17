package com.imjustdoom.justdoomapi.controller;

import com.google.gson.Gson;
import com.imjustdoom.justdoomapi.config.APIConfig;
import com.imjustdoom.justdoomapi.dto.in.ProjectCreateUpdateDto;
import com.imjustdoom.justdoomapi.dto.in.ProjectCreationDto;
import com.imjustdoom.justdoomapi.dto.in.ProjectEditDto;
import com.imjustdoom.justdoomapi.dto.out.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
            return SimpleProjectDto.create(blog.getTitle(), blog.getBlurb(), blog.getSlug(), latestUpdate.isEmpty() ? -1 : latestUpdate.get().getUploaded(), blog.getId());
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(projectDtos);
    }

    @GetMapping("{slug}")
    public ResponseEntity<?> project(@PathVariable("slug") String slug) {
        Optional<Project> project = projectRepository.findBySlug(slug);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        return ResponseEntity.ok().body(ProjectDto.create(project.get().getTitle(), project.get().getBlurb(), project.get().getDescription(), project.get().getCreated(), updateRepository.findAllByProjectId(project.get().getId()), project.get().getId()));
    }

    @GetMapping("{id}/title")
    public ResponseEntity<?> projectName(@PathVariable("id") int id) {
        Optional<Project> projectOptional = projectRepository.findById(id);

        if (projectOptional.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        return ResponseEntity.ok().body(ProjectNameDto.create(projectOptional.get().getTitle()));
    }

    @GetMapping("{id}/latest")
    public ResponseEntity<?> latestProjectUpdate(@PathVariable("id") int id) {
        Optional<Project> projectOptional = projectRepository.findById(id);

        if (projectOptional.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        Optional<Update> optionalUpdate = updateRepository.findFirstByProjectIdOrderByUploadedDesc(id);

        if (optionalUpdate.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Update not found"));
        }

        Update update = optionalUpdate.get();

        return ResponseEntity.ok().body(UpdateDto.create(update.getUploaded(), update.getDescription(), update.getTitle(), update.getFilename(), update.getVersions(), update.getSoftware(), update.getVersion(), update.getDownloads(), update.getStatus(), update.getId(), APIUtil.downloadLink(update.getId())));
    }

    @GetMapping("{id}/updates")
    public ResponseEntity<?> projectUpdates(@PathVariable("id") int id) {
        List<UpdateDto> updateList = updateRepository.findAllByProjectId(id).stream().map(update -> UpdateDto.create(update.getUploaded(), update.getDescription(), update.getTitle(), update.getFilename(), update.getVersions(), update.getSoftware(), update.getVersion(), update.getDownloads(), update.getStatus(), update.getId(), APIUtil.downloadLink(update.getId()))).toList();
        return ResponseEntity.ok().body(updateList);
    }

    // TODO: make it so only updates under the certain project can be seen under it
    @GetMapping("{slug}/updates/{updateId}")
    public ResponseEntity<?> update(@PathVariable("slug") String slug, @PathVariable("updateId") int updateId) {

        Optional<Project> project = projectRepository.findBySlug(slug);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        Optional<Update> optionalUpdate = updateRepository.findById(updateId);
        if (optionalUpdate.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Update not found"));
        }

        Update update = optionalUpdate.get();

        return ResponseEntity.ok().body(UpdateDto.create(update.getUploaded(), update.getDescription(), update.getTitle(), update.getFilename(), update.getVersions(), update.getSoftware(), update.getVersion(), update.getDownloads(), update.getStatus(), update.getId(), APIUtil.downloadLink(update.getId())));
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

        Project blogPost = new Project(dto.getTitle(), dto.getSlug(), dto.getDescription(), dto.getBlurb(), dto.isPublic(), account);
        projectRepository.save(blogPost);

        return ResponseEntity.ok().body(APIUtil.createSuccessResponse("Created a new project!"));
    }

    @PostMapping("{slug}/update")
    public ResponseEntity<?> createProjectUpdate(@PathVariable("slug") String slug, @RequestHeader("authorization") String token, @RequestParam("file") MultipartFile file, @RequestPart("data") String data) {

        ProjectCreateUpdateDto dto = new Gson().fromJson(data, ProjectCreateUpdateDto.class);

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!authService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        Optional<Project> project = projectRepository.findBySlug(slug);
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

    @PostMapping("{slug}/edit")
    public ResponseEntity<?> editProject(@RequestHeader("authorization") String token, @RequestBody ProjectEditDto dto) {
        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!authService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        projectRepository.updateProjectBySlug(dto.getTitle(), dto.getSlugupdate(), dto.getDescription(), dto.getBlurb(), dto.isPublic(), dto.getSlug());

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
