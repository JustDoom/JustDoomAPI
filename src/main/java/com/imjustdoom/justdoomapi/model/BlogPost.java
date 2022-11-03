package com.imjustdoom.justdoomapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table
@NoArgsConstructor
public class BlogPost {

    public BlogPost(String title, String blogPost, Account account) {
        this.blogPost = blogPost;
        this.account = account;
        this.created = System.currentTimeMillis();
        this.title = title;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private long created;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String blogPost;

    @Column
    private String title;

    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;
}