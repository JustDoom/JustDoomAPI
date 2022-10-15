package com.imjustdoom.justdoomapi.repository;

import com.imjustdoom.justdoomapi.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<BlogPost, Long> {
    Optional<BlogPost> findById(int id);
}