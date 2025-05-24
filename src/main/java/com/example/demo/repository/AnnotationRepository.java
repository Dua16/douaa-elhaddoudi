package com.example.demo.repository;

import com.example.demo.entities.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface AnnotationRepository extends JpaRepository<Annotation,Long> {
    boolean existsByAnnotateurIdAndCoupleTexteId(Long annotateurId, Long id);

    @Query("SELECT a.coupleTexte.id FROM Annotation a WHERE a.annotateur.id = :annotateurId")
    Set<Long> findIdsCouplesAnnotesParAnnotateur(Long annotateurId);
}
