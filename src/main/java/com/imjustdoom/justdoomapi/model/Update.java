package com.imjustdoom.justdoomapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "updates")
@NoArgsConstructor
public class Update {

    public Update(String title, String description, String filename, List<String> versions, List<String> software, String version, int downloads, String status, Project project) {
        this.description = description;
        this.uploaded = LocalDateTime.now();
        this.title = title;
        this.project = project;
        this.filename = filename;
        this.versions = versions;
        this.software = software;
        this.version = version;
        this.downloads = downloads;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDateTime uploaded;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @Column(nullable = false)
    private String filename;

    @ElementCollection
    private List<String> versions;

    @ElementCollection
    private List<String> software;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private int downloads;

    @Column(nullable = false)
    private String status;
}