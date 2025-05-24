package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnotationService {

    private final AnnotationRepository annotationRepository;
    private final TacheRepository tacheRepository;
    private final ClassePossibleRepository classePossibleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CoupleTexteRepository coupleTexteRepository;

    public List<ClassePossible> getClassesPossiblesByDataset(Long datasetId) {
        return classePossibleRepository.findByDatasetId(datasetId);
    }

    public List<CoupleTexte> getCouplesPourAnnotateurEtDataset(Long annotateurId, Long datasetId) {
        return tacheRepository.findByAnnotateurIdAndDatasetId(annotateurId, datasetId)
                .stream()
                .filter(tache -> tache.getCoupleTextes() != null)
                .flatMap(tache -> tache.getCoupleTextes().stream())
                .collect(Collectors.toList());
    }
    public List<Map<String, Object>> getAllAnnotationData() {
        List<Annotation> annotations = annotationRepository.findAll();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Annotation annotation : annotations) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", annotation.getId());
            row.put("annotateur_id", annotation.getAnnotateur().getId());
            row.put("couple_texte_id", annotation.getCoupleTexte().getId());
            row.put("classe_choisie", annotation.getClasseChoisie());
            row.put("nb_annotations", 1);
            data.add(row);
        }
        return data;
    }


    public void enregistrerAnnotation(Long annotateurId, Long coupleTexteId, String classeChoisie) {
        Annotation annotation = new Annotation();
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(annotateurId);
        annotation.setAnnotateur((Annotateur) utilisateurOpt.get());
        annotation.setCoupleTexte(coupleTexteRepository.findById(coupleTexteId).get());
        annotation.setClasseChoisie(classeChoisie);
        annotationRepository.save(annotation);

        CoupleTexte coupleTexte = coupleTexteRepository.findById(coupleTexteId).get();
        Tache tache = coupleTexte.getTache();

        tache.setNbCouples_annote(tache.getNbCouples_annote() + 1);

        int nbAnnote = tache.getNbCouples_annote();
        int taille = tache.getSize();

        if (taille > 0) {
            double avancement = (double) nbAnnote / taille * 100;
            tache.setAvancement(avancement);
        } else {
            tache.setAvancement(0.0);
        }

        tacheRepository.save(tache);
    }


    public Page<CoupleTexte> getCouplesPourAnnotateurEtDataset(Long annotateurId, Long datasetId, Pageable pageable) {
        Page<CoupleTexte> couples = coupleTexteRepository.findByDatasetId(datasetId, pageable);

        // Ajouter info si déjà annoté
        couples.forEach(c -> {
            boolean existe = annotationRepository.existsByAnnotateurIdAndCoupleTexteId(annotateurId, c.getId());
            c.setAnnotated(existe); // assure-toi que le champ 'annotated' existe dans CoupleTexte
        });

        return couples;
    }
    public Set<Long> getIdsCouplesAnnotesParAnnotateur(Long annotateurId) {
        return annotationRepository.findIdsCouplesAnnotesParAnnotateur(annotateurId);
    }

    public Page<CoupleTexte> getCouplesPourAnnotateurEtDatasett(Long annotateurId, Long datasetId, Pageable pageable) {
        return coupleTexteRepository.findByAnnotateurAndDatasett(annotateurId, datasetId, pageable);
    }
}
