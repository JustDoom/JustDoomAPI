package com.imjustdoom.justdoomapi.controller;

import com.google.gson.Gson;
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
import com.imjustdoom.justdoomapi.service.AccountService;
import com.imjustdoom.justdoomapi.util.APIUtil;
import com.imjustdoom.justdoomapi.util.HtmlUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ResourceApiController {

    private final ProjectRepository projectRepository;
    private final UpdateRepository updateRepository;
    private final TokenRepository tokenRepository;

    private final AccountService accountService;

    @GetMapping("projects")
    public ResponseEntity<?> projects() {
        List<SimpleProjectDto> projectDtos = projectRepository.findAllByIsPublic(true).stream().map(blog -> SimpleProjectDto.create(blog.getTitle(), blog.getBlurb(), blog.getId())).collect(Collectors.toList());
        return ResponseEntity.ok().body(projectDtos);
    }

    @GetMapping("admin/projects")
    public ResponseEntity<?> projectsAdmin(@RequestHeader Map<String, String> headers) {

        String token = headers.get("authorization");

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!accountService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        List<SimpleAdminProjectDto> projectDtos = projectRepository.findAll().stream().map(blog -> SimpleAdminProjectDto.create(blog.getTitle(), blog.getBlurb(), blog.isPublic(), blog.getId())).collect(Collectors.toList());
        return ResponseEntity.ok().body(projectDtos);
    }

    @GetMapping("admin/projects/{id}")
    public ResponseEntity<?> projectAdmin(@RequestHeader Map<String, String> headers, @PathVariable("id") int id) {

        String token = headers.get("authorization");

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!accountService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        return ResponseEntity.ok().body(AdminProjectDto.create(project.get().getTitle(), project.get().getBlurb(), project.get().getDescription(), project.get().isPublic(), project.get().getId()));
    }

    @GetMapping("projects/{id}")
    public ResponseEntity<?> project(@PathVariable("id") int id) {

        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        return ResponseEntity.ok().body(ProjectDto.create(project.get().getTitle(), project.get().getBlurb(), project.get().getDescription(), project.get().getCreated(), updateRepository.findAllByProjectId(project.get().getId()), project.get().getId()));
    }

    @PostMapping("admin/projects/create")
    public ResponseEntity<?> createProject(@RequestHeader Map<String, String> headers, @RequestBody ProjectCreationDto dto) {
        String token = headers.get("authorization");

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!accountService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        Project blogPost = new Project(dto.getTitle(), dto.getDescription(), dto.getBlurb(), dto.isPublic(), account);
        projectRepository.save(blogPost);

        return ResponseEntity.ok().body(APIUtil.createSuccessResponse("Created a new project!"));
    }

    @PostMapping("admin/projects/{id}/update")
    public ResponseEntity<?> createProjectUpdate(@PathVariable("id") int id, @RequestHeader Map<String, String> headers, @RequestParam("file") MultipartFile file, @RequestPart("data") String data) {

        System.out.println(file);
        String token = headers.get("authorization");

        ProjectCreateUpdateDto dto = new Gson().fromJson(data, ProjectCreateUpdateDto.class);

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!accountService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
        }

        Update update = new Update(dto.getTitle(), dto.getDescription(), dto.getFilename(), dto.getVersions(), dto.getSoftware(), dto.getVersion(), dto.getStatus(), project.get());
        updateRepository.save(update);

        return ResponseEntity.ok().body(APIUtil.createSuccessResponse("Created a new project!"));
    }

    @PostMapping("admin/projects/edit")
    public ResponseEntity<?> editProject(@CookieValue(name = "token", required = false) String token, @RequestBody ProjectEditDto dto) {
        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!accountService.doesAccountHaveAdminPermission(account)) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        projectRepository.updateProjectById(dto.getTitle(), dto.getDescription(), dto.getBlurb(), dto.isPublic(), dto.getId());

        return ResponseEntity.ok().body(APIUtil.createSuccessResponse("Edit the project!"));
    }

    @Bean
    public WebMvcConfigurer corsConfigurerResourceAPI() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/projects/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET");
            }
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurerResourceCreate() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/admin/projects/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedOriginPatterns("http://localhost:3000")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST");
            }
        };
    }
}
