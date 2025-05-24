package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "annotateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Annotateur extends Utilisateur {

    @OneToMany(mappedBy = "annotateur")
    private List<Tache> taches;
    @OneToMany(mappedBy = "annotateur")
    private List<Annotation> annotations = new ArrayList<>();
//    public Annotateur(Long annotateurId) {
//        this.setId(annotateurId); // méthode héritée de Utilisateur
//    }
}
