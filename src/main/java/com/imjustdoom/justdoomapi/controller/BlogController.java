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
import com.imjustdoom.justdoomapi.util.HtmlUtil;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final BlogRepository blogRepository;

    @GetMapping("blogs")
    public ResponseEntity<?> getBlogs() {
        // TODO: should list newest first

        List<SimpleBlogDto> blogs = blogRepository.findAll().stream().map(blog -> SimpleBlogDto.create(blog.getTitle(), blog.getAccount().getUsername(), blog.getCreated().toString(), blog.getId())).collect(Collectors.toList());
        Collections.reverse(blogRepository.findAll().stream().map(blog -> SimpleBlogDto.create(blog.getTitle(), blog.getAccount().getUsername(), blog.getCreated().toString(), blog.getId())).collect(Collectors.toList()));
        return ResponseEntity.ok().body(blogs);
    }

    @GetMapping("blogs/{id}")
    public ResponseEntity<?> getBlog(@PathVariable("id") int id) {
        if (blogRepository.findById(id).isEmpty()) {
            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Blog not found"));
        }

        BlogPost blog = blogRepository.findById(id).get();

        return ResponseEntity.ok().body(BlogDto.create(blog.getTitle(), blog.getBlogPost(), blog.getAccount().getUsername(), blog.getCreated().toString(), blog.getId()));
    }

    @Bean
    public WebMvcConfigurer corsConfigurerBlog() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/blogs/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET");
            }
        };
    }
}