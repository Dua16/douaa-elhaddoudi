package com.example.demo;

import com.example.demo.entities.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import com.example.demo.web.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public SecurityConfig(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // Utilisateur admin créé en mémoire


        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                var userAdmin = User.withUsername("admin")
                        .password(encoder.encode("password"))
                        .roles("ADMIN")
                        .build();
                // Si c’est l’admin, on le retourne directement
                if (username.equals("admin")) {
                    return userAdmin;
                }

                // Sinon, on va chercher en base (annotateur uniquement)
                Utilisateur utilisateur = utilisateurRepository.findByLogin(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
                System.out.println(utilisateur.getLogin());
                Long roleId = utilisateur.getRole().getId();

                if (roleId != 1L) {
                    throw new UsernameNotFoundException("Seuls les utilisateurs avec role_id = 1 sont autorisés ici");
                }

                return new User(
                        utilisateur.getLogin(),
                        utilisateur.getPassword(),
                        utilisateur.isActive(),
                        true, true, true,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANNOTATEUR"))
                );
            }
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        .requestMatchers("/admins/**").hasRole("ADMIN")  // ← ajoute ceci si admin va sur /admin/**
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .permitAll()
                )

                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
