package com.example.demo.repository;

import com.example.demo.entities.Annotateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.Tache;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface TacheRepository extends JpaRepository<Tache,Long> {


    List<Tache> findByAnnotateur(Annotateur annotateur);
    Page<Tache> findAll(Pageable pageable);
    List<Tache> findByAnnotateurIdAndDatasetId(Long annotateurId, Long datasetId);

    boolean existsByAnnotateurIdAndDatasetId(Long id, Long datasetId);
}
