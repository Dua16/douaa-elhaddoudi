package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CoupleTexte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texte1;
    private String texte2;

//    @OneToMany(mappedBy = "coupleTexte", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Tache> tache;

    @ManyToOne
    private Tache tache;

    public CoupleTexte(String texte1, String texte2) {
        this.texte1 = texte1;
        this.texte2 = texte2;
    }

    @ManyToOne
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

    public CoupleTexte(Long coupleTexteId) {
    }
    @OneToMany
    private List<Annotation> annotations = new ArrayList<>();

    @Transient
    private boolean annotated;


}
