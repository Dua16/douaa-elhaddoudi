package com.example.demo.web;

import com.example.demo.entities.ClassePossible;
import com.example.demo.entities.CoupleTexte;
import com.example.demo.services.AnnotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/annotation")
public class AnnotationController {

    private final AnnotationService annotationService;

    // Affiche la page d'annotation pour un annotateur et un dataset
    @GetMapping("/annotateurs/{annotateurId}/{datasetId}")
    public String afficherPageAnnotation(@PathVariable Long annotateurId,
                                         @PathVariable Long datasetId,
                                         @RequestParam(defaultValue = "0") int page,
                                         Model model) {

        int taillePage = 10; // 10 lignes par page

        Page<CoupleTexte> couplesPage = annotationService.getCouplesPourAnnotateurEtDatasett(annotateurId, datasetId, PageRequest.of(page, taillePage));
        List<ClassePossible> classesPossibles = annotationService.getClassesPossiblesByDataset(datasetId);
        Set<Long> couplesAnnotes = annotationService.getIdsCouplesAnnotesParAnnotateur(annotateurId);

        model.addAttribute("annotateurId", annotateurId);
        model.addAttribute("datasetId", datasetId);
        model.addAttribute("couples", couplesPage);
        model.addAttribute("classes", classesPossibles);
        model.addAttribute("couplesAnnotes", couplesAnnotes);
        model.addAttribute("pageCourante", page);
        model.addAttribute("totalPages", couplesPage.getTotalPages());

        return "annotateurs/annotation_page";
    }

    // Enregistre l'annotation soumise par le formulaire

    @PostMapping("/annotateurs/soumettre")
    public String enregistrerAnnotation(@RequestParam Long annotateurId,
                                        @RequestParam Long coupleTexteId,
                                        @RequestParam String classeChoisie,
                                        @RequestParam Long datasetId) {
        annotationService.enregistrerAnnotation(annotateurId, coupleTexteId, classeChoisie);
        // Redirige vers la même page pour annoter d'autres textes
        return "redirect:/annotation/annotateurs/" + annotateurId + "/" + datasetId;
    }
}
