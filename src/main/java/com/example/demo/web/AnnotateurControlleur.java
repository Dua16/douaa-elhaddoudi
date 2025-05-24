package com.example.demo.web;

import com.example.demo.entities.Tache;
import com.example.demo.services.AnnotateurService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AnnotateurControlleur {

    private final AnnotateurService annotateurService;

    public AnnotateurControlleur(AnnotateurService annotateurService) {
        this.annotateurService = annotateurService;
    }
    @GetMapping("/annotateurs/index")
    public String annotateurIndex(Model model) {
        var annotateur = annotateurService.getAnnotateurConnecte();
        model.addAttribute("annotateurNom", annotateur.getNom());
        List<Tache> taches = annotateurService.getTachesAnnotateurConnecte();
        model.addAttribute("taches", taches);

        return "annotateurs/index";
    }

    @GetMapping("/annotateurs/taches")
    public String afficherTachesAnnotateur(Model model) {
        List<Tache> taches = annotateurService.getTachesAnnotateurConnecte();

        model.addAttribute("taches", taches);

        return "annotateurs/taches";
    }
}
