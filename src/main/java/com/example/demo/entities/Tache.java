package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "annotateur_id")
    private Annotateur annotateur;

//    @ManyToOne
//    @JoinColumn(name = "couple_texte_id")  // FK vers CoupleTexte
//    private CoupleTexte coupleTexte;
//
    @OneToMany(mappedBy = "tache")
    private List<CoupleTexte> coupleTextes =new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;

    @Column(name = "date_affectation")
    private java.time.LocalDateTime dateAffectation;

    @Column(name = "avancement")
    private Double avancement = 0.0 ;

    @Column(name = "nbCouples_annote")
    private Integer nbCouples_annote=0;

    private Integer size;

    @PrePersist
    protected void onCreate() {
        dateAffectation = java.time.LocalDateTime.now();
    }
    // ✅ CHAMP À AJOUTER
    private boolean annule = false;

    public boolean getAnnule() {
        return this.annule;
    }

    public boolean isNonCommencee() {
        return avancement == null || avancement == 0.0;
    }

    public boolean isTerminee() {
        return avancement != null && avancement >= 100.0;
    }

    public boolean isEnCours() {
        return avancement != null && avancement > 0.0 && avancement < 100.0;
    }

    // Méthodes pour Thymeleaf (statut texte et classe CSS)
    public String getStatusTexte() {
        if (isNonCommencee()) return "Non commencée";
        if (isTerminee()) return "Terminée";
        return "En cours";
    }

    public String getStatusClasse() {
        if (isNonCommencee()) return "status-non-commencee";
        if (isTerminee()) return "status-termine";
        return "status-en-cours";
    }

}
