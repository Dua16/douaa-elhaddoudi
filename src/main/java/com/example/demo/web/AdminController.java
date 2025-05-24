package com.example.demo.web;

import com.example.demo.entities.Annotateur;
import com.example.demo.entities.CoupleTexte;
import com.example.demo.entities.Dataset;
import com.example.demo.entities.Tache;
import com.example.demo.repository.AnnotateurRepository;
import com.example.demo.repository.DatasetRepository;
import com.example.demo.services.AdminService;
import com.example.demo.services.AnnotateurService;
import com.example.demo.services.AnnotationService;
import com.example.demo.services.DatasetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Autowired
    private final AdminService adminService;
    @Autowired
    private AnnotationService annotationService;


    @Autowired
    private AnnotateurRepository annotateurRepository;
    @Autowired
    private DatasetService datasetService;
    // Injection de dépendances via le constructeur
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/admins/index")
    public String adminIndex() {
        return "admins/index";  // correspond à src/main/resources/templates/admins/index.html
    }

   @GetMapping("/admins/annotateurs")
        public String afficherAnnotateurs(Model model) {
            List<Annotateur> annotateurs = adminService.listerAnnotateurs();
            model.addAttribute("annotateurs", annotateurs);
            return "admins/annotateurs";
        }

    @GetMapping("/admins/annotateurs/nouveau")
    public String nouveauAnnotateurForm(Model model) {
        model.addAttribute("annotateur", new Annotateur());
        return "admins/annotateurs/nouveau";  // Affiche juste le formulaire
    }

    @PostMapping("/admins/annotateurs/enregistrer")
    public String enregistrerAnnotateur(
            @ModelAttribute("annotateur") Annotateur annotateur ) {
        adminService.ajouterAnnotateur(annotateur.getNom(), annotateur.getPrenom(), annotateur.getLogin(), annotateur.getPassword());
        return "redirect:/admins/annotateurs";
    }

    @GetMapping("/admins/annotateurs/modifier/{id}")
    public String modifierAnnotateurForm(@PathVariable Long id, Model model) {
        Annotateur annotateur = adminService.rechercherAnnotateurParId(id);
        model.addAttribute("annotateur", annotateur);
        return "admins/annotateurs/modifier"; // assure-toi que le fichier HTML existe
    }
    @PostMapping("/admins/annotateurs/modif")
    public String modifAnnotateur(@ModelAttribute("annotateur") Annotateur annotateur) {
        adminService.modifierAnnotateur(
                annotateur.getId(),
                annotateur.getNom(),
                annotateur.getPrenom(),
                annotateur.getLogin(),
                annotateur.getPassword());
        return "redirect:/admins/annotateurs";
    }
    @GetMapping("/admins/annotateurs/supprimer/{id}")
    public String supprimerAnnotateur(@PathVariable Long id) {
        adminService.supprimerAnnotateur(id);
        return "redirect:/admins/annotateurs";
    }


    @GetMapping("/admins/importer")
    public String importerTextes() {
//        try {
//            List<CoupleTexte> couples = adminService.importerTextesEtRetournerListe(file);
//            model.addAttribute("couples", couples);
//            model.addAttribute("nomFichier", file.getOriginalFilename());
//            model.addAttribute("taille", couples.size());
//            model.addAttribute("classePossibles", adminService.getAllClassesPossibles());
//            return "admins/importer_result";
//        } catch (Exception e) {
//            model.addAttribute("message", "Erreur lors de l'importation : " + e.getMessage());
//
//        }
        return "admins/importer_result";
    }
    @PostMapping("/admins/valider-importation")
    public String validerImportation(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @RequestParam("nomClassePossible") String nomClassePossible,
            Model model  // ajouter Model pour passer des attributs à la vue
    ) {
        try {

            Dataset dataset = adminService.enregistrerDatasetEtTachesParNomClasse(file.getOriginalFilename(), description, nomClassePossible);
            model.addAttribute("successMessage", "✅ L'importation a été enregistrée avec succès !");
            List<CoupleTexte> couples = adminService.importerTextesEtRetournerListe(file, dataset);
            model.addAttribute("couples", couples);
            model.addAttribute("nomFichier", file.getOriginalFilename());
            model.addAttribute("taille", couples.size());
            model.addAttribute("classePossibles", adminService.getAllClassesPossibles());
            return "admins/importer_result";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "❌ Erreur lors de l'enregistrement : " + e.getMessage());
        }
        return "admins/importer";  // reste sur la même page avec message
    }

//



//    @GetMapping("/admins/importer")
//    public String afficherImportPage() {
//        return "admins/importer";
//    }

    @GetMapping("/admins/attribution_result")
    public String attribuerAutomatiquement(Model model) {
         int nbTachesAttribuees = adminService.attribuerAleatoirementTachesAuxAnnotateurs();
          model.addAttribute("nbTaches", nbTachesAttribuees);
        return "admins/attribution_result";
    }
    @GetMapping("/admins/annotateurs/desactives")
    public String voirAnnotateursDesactives(Model model) {
        List<Annotateur> annotateursDesactives = adminService.listerAnnotateursDesactives();
        model.addAttribute("annotateurs", annotateursDesactives);
        return "admins/annotateurs/desactives";
    }

    @PostMapping("/admins/annotateurs/activer")
    public String activerAnnotateursSelectionnes(@RequestParam("annotateurIds") List<Long> ids, RedirectAttributes redirectAttributes) {
        adminService.activerAnnotateurs(ids);
        redirectAttributes.addFlashAttribute("message", "Annotateurs activés avec succès !");
        return "redirect:/admins/annotateurs/desactives";
    }

    @GetMapping("/admins/attribution_form")
    public String afficherFormulaireAttribution(Model model) {
        List<Dataset> dataset=  datasetService.getAll();
        List<Annotateur> Annotateurs = adminService.listerAnnotateurs();

        model.addAttribute("datasets", dataset);
        model.addAttribute("annotateurs", Annotateurs);
        return "admins/attribution_form";
    }

    @GetMapping("/admins/suivi_taches")
    public String afficherSuiviTaches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Tache> pageTaches = adminService.getTachesPage(pageable);

        model.addAttribute("taches", pageTaches.getContent());
        model.addAttribute("pageCourante", page);
        model.addAttribute("totalPages", pageTaches.getTotalPages());
        return "admins/suivi_taches";
    }


    @PostMapping("/admins/attribution")
    public String lancerAttribution(@RequestParam("annotateurIds") List<Long> annotateurIds,
                                    @RequestParam("datasetId") Long datasetId,
                                    Model model) {


        adminService.attribuerTextesAleatoirement(annotateurIds, datasetId);
        model.addAttribute("message", "Textes attribués avec succès !");
        return "admins/attribution_result";
    }
//
@PostMapping("/admins/export_annotations")
public void exportAnnotations(@RequestParam("format") String format, HttpServletResponse response) throws IOException {
    List<Map<String, Object>> data = annotationService.getAllAnnotationData();

    if ("csv".equalsIgnoreCase(format)) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=annotations.csv");
        writeCsv(data, response.getWriter());
    } else {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=annotations.json");
        new ObjectMapper().writeValue(response.getOutputStream(), data);
    }
}

    private void writeCsv(List<Map<String, Object>> data, Writer writer) throws IOException {
        if (data.isEmpty()) return;

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader(data.get(0).keySet().toArray(new String[0])))) {
            for (Map<String, Object> row : data) {
                csvPrinter.printRecord(row.values());
            }
        }
    }

    @GetMapping("/admins/export_page")
    public String showExportPage() {
        return "admins/export_annotations";
    }


}
