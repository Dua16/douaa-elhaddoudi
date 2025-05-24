package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name="role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_role")
    private String nomRole;

    @OneToMany(mappedBy = "role")
    private List<Utilisateur> utilisateurs;
}
