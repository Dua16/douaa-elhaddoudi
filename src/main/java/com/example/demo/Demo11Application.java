package com.example.demo;

import com.example.demo.entities.Admin;
import com.example.demo.entities.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Demo11Application implements CommandLineRunner {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UtilisateurRepository utilisateurRepository;
    public static void main(String[] args) {
        SpringApplication.run(Demo11Application.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        Role role1 = new Role();
        role1.setNomRole("ROLE_ANNOTATEUR");
        roleRepository.save(role1);
        Role role2 = new Role();
        role2.setNomRole("ROLE_ADMIN");
        roleRepository.save(role2);
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        Admin admin = new Admin();
//        admin.setNom("admin");
//        admin.setPrenom("admin");
//        admin.setLogin("admin@ensah");
//        admin.setPassword(encoder.encode("admin"));
//        admin.setRole(roleRepository.findById(2L).orElse(null));
//        System.out.println("Saving admin...");
//        utilisateurRepository.save(admin);
//        System.out.println("Saved admin.");
    }
}
