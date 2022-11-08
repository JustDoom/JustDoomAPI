package com.imjustdoom.justdoomapi.repository;

import com.imjustdoom.justdoomapi.model.Update;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpdateRepository extends JpaRepository<Update, Integer> {

    List<Update> findAllByProjectId(int id);

    List<Update> findAllByProjectIdAndStatus(int id, String status);

    // Get last updated update
    Update findFirstByProjectIdOrderByUploadedDesc(int id);
}