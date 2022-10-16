package com.imjustdoom.justdoomapi.controller;

import com.imjustdoom.justdoomapi.dto.in.BlogPostDto;
import com.imjustdoom.justdoomapi.dto.in.LoginDto;
import com.imjustdoom.justdoomapi.dto.in.RegisterDto;
import com.imjustdoom.justdoomapi.model.Account;
import com.imjustdoom.justdoomapi.model.BlogPost;
import com.imjustdoom.justdoomapi.repository.AccountRepository;
import com.imjustdoom.justdoomapi.repository.BlogRepository;
import com.imjustdoom.justdoomapi.repository.ProjectRepository;
import com.imjustdoom.justdoomapi.repository.TokenRepository;
import com.imjustdoom.justdoomapi.service.AccountService;
import com.imjustdoom.justdoomapi.util.APIUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController()
@RequiredArgsConstructor
public class AuthController {

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final BlogRepository blogRepository;
    private final ProjectRepository projectRepository;

    private final AccountService accountService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginDto data, @CookieValue(name = "token", required = false) String token) {
        return accountService.login(data, token);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto data, @CookieValue(name = "token", required = false) String token) {
        return accountService.register(data, token);
    }

    @PostMapping("/auth/logout2")
    public ResponseEntity<?> logout() {
        return accountService.logout();
    }

    // TODO: move to blog controller
    @PostMapping("/auth/post-blog")
    public ResponseEntity<?> post(@CookieValue(name = "token", required = false) String token, @RequestBody BlogPostDto dto) {

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
    public WebMvcConfigurer corsConfigurerAuth() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/auth/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedOriginPatterns("http://localhost:3000")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST");
            }
        };
    }
}