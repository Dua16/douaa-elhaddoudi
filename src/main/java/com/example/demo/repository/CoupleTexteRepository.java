package com.example.demo.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.CoupleTexte;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoupleTexteRepository extends JpaRepository<CoupleTexte,Long> {

    Page<CoupleTexte> findByDatasetId(Long datasetId, Pageable pageable);
    @Query("SELECT ct FROM CoupleTexte ct " +
            "JOIN ct.tache t " +
            "WHERE t.annotateur.id = :annotateurId AND t.dataset.id = :datasetId")
    Page<CoupleTexte> findByAnnotateurAndDatasett(@Param("annotateurId") Long annotateurId,
                                                 @Param("datasetId") Long datasetId,
                                                 Pageable pageable);


}
