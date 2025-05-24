package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String classeChoisie;  // Le champ classeChoisie reste une String


    @ManyToOne
    @JoinColumn(name = "couple_texte_id")
    private CoupleTexte coupleTexte;

    @ManyToOne
    @JoinColumn(name = "annotateur_id")
    private Annotateur annotateur;

    private Integer nb_annotations =0;
}
