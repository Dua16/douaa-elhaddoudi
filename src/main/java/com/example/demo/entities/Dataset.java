package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomDataset;
    private String description;

    @OneToMany(mappedBy = "dataset")
    private List<Tache> taches;

    private int taille;
    private Double avancement;

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL)
    private List<CoupleTexte> couples = new ArrayList<>();

    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL)
    private List<ClassePossible> classesPossible;

}
