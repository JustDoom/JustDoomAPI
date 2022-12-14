package com.imjustdoom.justdoomapi.repository;

import com.imjustdoom.justdoomapi.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findAllByIsPublic(boolean isPublic);

    Optional<Project> findBySlug(String slug);

    @Modifying
    @Transactional
    @Query("UPDATE Project project SET project.title = ?1, project.slug = ?2, project.description = ?3, project.blurb = ?4, project.isPublic = ?5 WHERE project.slug = ?6")
    void updateProjectById(String title, String slugupdate, String description, String blurb, boolean isPublic, int id);

    @Modifying
    @Transactional
    @Query("UPDATE Project project SET project.title = ?1, project.slug = ?2, project.description = ?3, project.blurb = ?4, project.isPublic = ?5 WHERE project.slug = ?6")
    void updateProjectBySlug(String title, String slugupdate, String description, String blurb, boolean isPublic, String slug);
}