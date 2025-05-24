package com.example.demo.repository;

import com.example.demo.entities.Annotateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnotateurRepository extends JpaRepository<Annotateur,Long> {
    Optional<Annotateur> findByLogin(String username);;
}
