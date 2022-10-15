package com.imjustdoom.justdoomapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table
@NoArgsConstructor
public class BlogPost {

    public BlogPost(String title, String blogPost, Account account) {
        this.blogPost = blogPost;
        this.account = account;
        this.created = LocalDateTime.now();
        this.title = title;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String blogPost;

    @Column
    private String title;

    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;
}