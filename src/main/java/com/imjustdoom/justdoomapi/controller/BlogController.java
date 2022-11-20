package com.imjustdoom.justdoomapi.controller;

import com.imjustdoom.justdoomapi.dto.in.BlogPostDto;
import com.imjustdoom.justdoomapi.dto.out.BlogDto;
import com.imjustdoom.justdoomapi.dto.out.SimpleBlogDto;
import com.imjustdoom.justdoomapi.model.Account;
import com.imjustdoom.justdoomapi.model.BlogPost;
import com.imjustdoom.justdoomapi.repository.AccountRepository;
import com.imjustdoom.justdoomapi.repository.BlogRepository;
import com.imjustdoom.justdoomapi.repository.TokenRepository;
import com.imjustdoom.justdoomapi.util.APIUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final BlogRepository blogRepository;

    @GetMapping
    public ResponseEntity<?> getBlogs() {
        List<SimpleBlogDto> blogs = blogRepository.findAllByIsPublic(true).stream().map(blog -> SimpleBlogDto.create(blog.getTitle(), blog.getAccount().getUsername(), blog.getCreated(), blog.getId())).collect(Collectors.toList());
        Collections.reverse(blogRepository.findAll().stream().map(blog -> SimpleBlogDto.create(blog.getTitle(), blog.getAccount().getUsername(), blog.getCreated(), blog.getId())).collect(Collectors.toList()));
        return ResponseEntity.ok().body(blogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlog(@PathVariable("id") int id) {
        if (blogRepository.findById(id).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Blog not found"));
        }

        BlogPost blog = blogRepository.findById(id).get();

        return ResponseEntity.ok().body(BlogDto.create(blog.getTitle(), blog.getBlogPost(), blog.getAccount().getUsername(), blog.getCreated(), blog.getId()));
    }

    @PostMapping("/create")
    public ResponseEntity<?> post(@RequestHeader("authorization") String token, @RequestBody BlogPostDto dto) {

        if (tokenRepository.findByToken(token).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not logged in."));
        }

        Account account = tokenRepository.findByToken(token).get().getAccount();

        if (!account.getRole().equals("ADMIN")) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("You are not an admin"));
        }

        BlogPost blogPost = new BlogPost(dto.getTitle(), dto.getPost(), account);
        blogRepository.save(blogPost);

        return ResponseEntity.ok().body(APIUtil.createSuccessResponse("Posted blog"));
    }

    @Bean
    public WebMvcConfigurer corsConfigurerBlog() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/blogs/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST");
            }
        };
    }
}