package com.imjustdoom.justdoomapi.repository;

import com.imjustdoom.justdoomapi.model.Project;
import com.imjustdoom.justdoomapi.model.Update;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateRepository extends JpaRepository<Update, Integer> {
}