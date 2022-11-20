//package com.imjustdoom.justdoomapi.model;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Getter
//@Entity
//@Table
//@NoArgsConstructor
//public class SlugHistory {
//
//    public SlugHistory(String slug, Project project) {
//        this.project = project;
//        this.updated = System.currentTimeMillis();
//        this.slug = slug;
//    }
//
//    @Id
//    @ManyToOne(cascade = CascadeType.ALL)
//    private Project project;
//
//    @Column(nullable = false)
//    private long updated;
//
//    @Column(nullable = false)
//    private String slug;
//}