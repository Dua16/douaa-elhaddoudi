package com.example.demo.services;

import com.example.demo.entities.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private UtilisateurRepository utilisateurRepository;
    private RoleRepository roleRepository;
    private CoupleTexteRepository coupleTexteRepository;
    private TacheRepository tacheRepository;
    private AnnotationRepository annotationRepository;
    private PasswordEncoder passwordEncoder;
    private DatasetRepository datasetRepository;
    private AnnotateurRepository annotateurRepository;

    @Autowired
    private ClassePossibleRepository classePossibleRepository;


    @Autowired
    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Autowired
    public void setAnnotateurRepository(AnnotateurRepository annotateurRepository) {
        this.annotateurRepository = annotateurRepository;
    }

    @Autowired
    public void setDatasetRepository(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setCoupleTexteRepository(CoupleTexteRepository coupleTexteRepository) {
        this.coupleTexteRepository = coupleTexteRepository;
    }

    @Autowired
    public void setTacheRepository(TacheRepository tacheRepository) {
        this.tacheRepository = tacheRepository;
    }

    @Autowired
    public void setAnnotationRepository(AnnotationRepository annulationRepository) {
        this.annotationRepository = annotationRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {

        this.passwordEncoder = passwordEncoder;
    }
//    private String genererMotDePasseAleatoire(int longueur) {
//        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";
//        SecureRandom random = new SecureRandom();
//        StringBuilder motDePasse = new StringBuilder(longueur);
//        for (int i = 0; i < longueur; i++) {
//            int index = random.nextInt(caracteres.length());
//            motDePasse.append(caracteres.charAt(index));
//        }
//        return motDePasse.toString();
//    }

    public Annotateur ajouterAnnotateur(String nom, String prenom, String login, String motDePasse) {
        if (utilisateurRepository.findByLogin(login).isPresent()) {
            throw new RuntimeException("Login déjà utilisé");
        }

        Annotateur annotateur = new Annotateur();
        annotateur.setNom(nom);
        annotateur.setPrenom(prenom);
        annotateur.setLogin(login);
        annotateur.setPassword(passwordEncoder.encode(motDePasse));
        annotateur.setRole(roleRepository.findByNomRole("ROLE_ANNOTATEUR"));

        Annotateur savedAnnotateur = annotateurRepository.save(annotateur);
        return (Annotateur) utilisateurRepository.save(annotateur);
    }

    public Annotateur modifierAnnotateur(Long id, String nom, String prenom, String login, String motDePasse) {
        // Vérifie que l'utilisateur existe et est un Annotateur
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
        if (utilisateurOpt.isEmpty() || !(utilisateurOpt.get() instanceof Annotateur) || !utilisateurRepository.findByLogin(login).get().isActive()) {
            throw new RuntimeException("Annotateur non trouvé");
        }

        Annotateur annotateur = (Annotateur) utilisateurOpt.get();

        // Vérifie si un autre utilisateur utilise déjà ce login
        Optional<Utilisateur> utilisateurAvecLogin = utilisateurRepository.findByLogin(login);
        if (utilisateurAvecLogin.isPresent() && !utilisateurAvecLogin.get().getId().equals(id)) {
            throw new RuntimeException("Ce login est déjà utilisé par un autre utilisateur");
        }

        // Mise à jour des champs
        annotateur.setNom(nom);
        annotateur.setPrenom(prenom);
        annotateur.setLogin(login);

        if (motDePasse != null && !motDePasse.isBlank()) {
            annotateur.setPassword(passwordEncoder.encode(motDePasse));
        }

        // Sauvegarde
        return (Annotateur) utilisateurRepository.save(annotateur);
    }


    public void supprimerAnnotateur(Long id) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            utilisateur.setActive(false);
            utilisateurRepository.save(utilisateur);
        }

    }

    public List<Annotateur> listerAnnotateurs() {
        return utilisateurRepository.findByActiveTrue();
    }


    public List<Tache> getAllTaches() {
        return tacheRepository.findAll();
    }
    public Annotateur rechercherAnnotateurParId(Long id) {
        return utilisateurRepository.findById(id)
                .filter(u -> u instanceof Annotateur)
                .map(u -> (Annotateur) u)
                .orElseThrow(() -> new RuntimeException("Annotateur non trouvé"));
    }



    private final ObjectMapper objectMapper = new ObjectMapper();
    public List<CoupleTexte> importerTextesEtRetournerListe(MultipartFile file,Dataset data) throws IOException {
        List<CoupleTexte> couples;
        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new RuntimeException("Nom de fichier invalide");
        }

        if (filename.endsWith(".csv")) {
            couples = importerTextesCSV(file,data);
            data.setTaille(couples.size());
            datasetRepository.save(data);
        } else if (filename.endsWith(".json")) {
            couples = importerTextesJSON(file,data);
            data.setTaille(couples.size());
            datasetRepository.save(data);
        } else {
            throw new RuntimeException("Format de fichier non supporté (CSV ou JSON uniquement)");
        }


        return couples;
    }


    private List<CoupleTexte> importerTextesCSV(MultipartFile file,Dataset data) throws IOException {
        List<CoupleTexte> couples = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withTrim().withIgnoreEmptyLines().withFirstRecordAsHeader())
        ) {
            for (CSVRecord record : csvParser) {
                String texte1 = record.get(1);
                String texte2 = record.get(2);
                CoupleTexte couple = new CoupleTexte(texte1, texte2);
                couple.setDataset(data);
                couple = coupleTexteRepository.save(couple);
                couples.add(couple);
            }
        }

        return couples;
    }

    public Page<Tache> getTachesPaginees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateAffectation").descending());
        return  tacheRepository.findAll(pageable);
    }


    private List<CoupleTexte> importerTextesJSON(MultipartFile file,Dataset data) throws IOException {
        JsonNode root = objectMapper.readTree(file.getInputStream());
        List<CoupleTexte> couples = new ArrayList<>();

        for (JsonNode node : root) {
            String texte1 = node.has("texte1") ? node.get("texte1").asText() : null;
            String texte2 = node.has("texte2") ? node.get("texte2").asText() : null;

            if (texte1 != null && texte2 != null) {
                CoupleTexte couple = new CoupleTexte(texte1, texte2);
                couple.setDataset(data);
                couple = coupleTexteRepository.save(couple);
                couples.add(couple);
            }
        }

        return couples;
    }

    @Transactional
    public void attribuerTextesAleatoirement(List<Long> annotateurIds, Long datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new IllegalArgumentException("Dataset introuvable"));

        List<Annotateur> annotateurs = annotateurRepository.findAllById(annotateurIds);
        List<CoupleTexte> couples = new ArrayList<>(dataset.getCouples());

        Collections.shuffle(couples);

        int totalAnnotateurs = annotateurs.size();
        int totalCouples = couples.size();
        int tailleLot = totalCouples / totalAnnotateurs;

        for (int i = 0; i < annotateurs.size(); i++) {
            Annotateur annotateur = annotateurs.get(i);
            System.out.println(annotateur.getNom());
            // ⚠️ Vérifie si une tâche existe déjà pour cet annotateur et dataset
            boolean dejaAffectee = tacheRepository.existsByAnnotateurIdAndDatasetId(annotateur.getId(), datasetId);
            if (dejaAffectee) {
                System.out.println("Tâche déjà affectée pour l'annotateur " + annotateur.getId());
                continue;
            }

            Tache tache = new Tache();
            tache.setAnnotateur(annotateur);
            tache.setDataset(dataset);
            tache.setAnnule(false);
            tache.setDateAffectation(LocalDateTime.now());

            int fromIndex = i * tailleLot;
            int toIndex = (i == annotateurs.size() - 1) ? totalCouples : fromIndex + tailleLot;

            List<CoupleTexte> sousListe = couples.subList(fromIndex, toIndex);

            tache.setCoupleTextes(new ArrayList<>());
            tache = tacheRepository.save(tache); // Sauvegarde avant de lier les couples

            for (CoupleTexte couple : sousListe) {
                couple.setTache(tache);
                coupleTexteRepository.save(couple);
                tache.getCoupleTextes().add(couple);
            }

            tache.setAvancement(0.0);
            tache.setSize(sousListe.size());
            tacheRepository.save(tache);
        }
    }


    public int attribuerAleatoirementTachesAuxAnnotateurs() {
        List<CoupleTexte> couples = coupleTexteRepository.findAll();
        List<Annotateur> annotateurs = listerAnnotateurs(); // prend les actifs uniquement

        if (annotateurs.isEmpty() || couples.isEmpty()) return 0;

        Random random = new Random();
        List<Tache> tachesAAjouter = new ArrayList<>();

        for (CoupleTexte couple : couples) {
            Annotateur annotateur = annotateurs.get(random.nextInt(annotateurs.size()));
            Tache tache = new Tache();
            tache.setAnnotateur(annotateur);
            tache.setDateAffectation(LocalDateTime.now());
            tache.setAnnule(false);
            tachesAAjouter.add(tache); // ✅ AJOUT DANS LA LISTE
        }

        return tachesAAjouter.size(); // ici tu auras une taille > 0
    }

    public List<ClassePossible> getAllClassesPossibles() {
        return classePossibleRepository.findAll();
    }

    public Dataset enregistrerDatasetEtTachesParNomClasse(String nomFichier,String description, String nomClassesConcatenees) {
        try {
            if (nomClassesConcatenees == null || nomClassesConcatenees.trim().isEmpty()) {
                throw new IllegalArgumentException("Le champ des classes possibles ne peut pas être vide.");
            }

            // Étape 1 : Créer et sauvegarder le Dataset
            Dataset dataset = new Dataset();
            dataset.setNomDataset(nomFichier); // ou prends un nom dynamique
            dataset.setDescription(description != null ? description.trim() : "");
            dataset.setAvancement(0.0);
            dataset = datasetRepository.save(dataset); // Hibernate génère l’ID ici

            // Étape 2 : Séparer les classes possibles par ';' et les sauvegarder
            String[] classes = nomClassesConcatenees.split(";");
            for (String classeTexte : classes) {
                String propre = classeTexte.trim();
                if (!propre.isEmpty()) {
                    ClassePossible classe = new ClassePossible();
                    classe.setTextClasse(propre);
                    classe.setDataset(dataset); // 🔗 Associer à ce dataset
                    classePossibleRepository.save(classe);
                }
            }

            return dataset;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'enregistrement du dataset et des classes possibles : " + e.getMessage(), e);
        }
    }


    public List<Annotateur> listerAnnotateursDesactives() {
        return utilisateurRepository.findAll().stream()
                .filter(u -> u instanceof Annotateur)
                .map(u -> (Annotateur) u)
                .filter(annotateur -> !annotateur.isActive())  // Seulement désactivés
                .collect(Collectors.toList());
    }

    public void activerAnnotateurs(List<Long> ids) {
        for (Long id : ids) {
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
            if (utilisateurOpt.isPresent() && utilisateurOpt.get() instanceof Annotateur) {
                Annotateur annotateur = (Annotateur) utilisateurOpt.get();
                annotateur.setActive(true);
                utilisateurRepository.save(annotateur);
            }
        }
    }

    public Page<Tache> getTachesPage(Pageable pageable) {
        return tacheRepository.findAll(pageable);
    }



}