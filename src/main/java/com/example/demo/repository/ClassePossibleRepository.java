package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.ClassePossible;

import java.util.List;
import java.util.Optional;

public interface ClassePossibleRepository extends JpaRepository<ClassePossible,Long> {
    Optional<ClassePossible> findById(Long id);

    Optional<Object> findByTextClasse(String classePossibleNom);

    List<ClassePossible> findByDatasetId(Long datasetId);
}
