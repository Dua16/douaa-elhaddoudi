package com.example.demo.services;


import com.example.demo.entities.Annotateur;
import com.example.demo.entities.Tache;
import com.example.demo.repository.AnnotateurRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnnotateurService {

    private final AnnotateurRepository annotateurRepository;

    public AnnotateurService(AnnotateurRepository annotateurRepository) {
        this.annotateurRepository = annotateurRepository;
    }

    /**
     * Récupère l'annotateur connecté depuis le contexte de sécurité.
     */
    public Annotateur getAnnotateurConnecte() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return annotateurRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Annotateur non trouvé: " + username));
    }

    /**
     * Récupère les tâches affectées à l'annotateur connecté.
     */
    public List<Tache> getTachesAnnotateurConnecte() {
        Annotateur annotateur = getAnnotateurConnecte();
        // Supposons qu'Annotateur a une relation OneToMany vers Tache, méthode getTaches()
        return annotateur.getTaches();
    }
}

