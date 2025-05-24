package com.example.demo.repository;

import com.example.demo.entities.Annotateur;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.Utilisateur;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByLogin(String login);
    @Modifying
    @Transactional
    @Query("UPDATE Utilisateur u SET u.nom = :nom, u.prenom = :prenom, u.login = :login WHERE u.id = :id")
    int updateUtilisateurById(Long id, String nom, String prenom, String login);
    List<Annotateur> findByActiveTrue();
    Optional<Utilisateur> findById(Long id);


//    Annotateur findById(Long annotateurId);
}
