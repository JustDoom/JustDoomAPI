package com.imjustdoom.justdoomapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Table
@NoArgsConstructor
public class Project {

    public Project(String title, String description, String blurb, boolean isPublic, Account account) {
        this.description = description;
        this.blurb = blurb;
        this.account = account;
        this.created = System.currentTimeMillis();
        this.title = title;
        this.isPublic = isPublic;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private long created;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column
    private String blurb;

    @Column
    private String title;

    @Column
    private boolean isPublic;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Update> updates;

    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;
}