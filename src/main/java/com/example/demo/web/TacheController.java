package com.example.demo.web;

import com.example.demo.entities.Tache;
import com.example.demo.services.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TacheController {

    private AdminService adminService;

    public TacheController(AdminService adminService) {
        this.adminService= adminService;
    }


    public static class TacheAvecDateLimite {
        private final Tache tache;
        private final LocalDateTime dateLimite;

        public TacheAvecDateLimite(Tache tache, LocalDateTime dateLimite) {
            this.tache = tache;
            this.dateLimite = dateLimite;
        }

        public Tache getTache() { return tache; }
        public LocalDateTime getDateLimite() { return dateLimite; }
    }
}
