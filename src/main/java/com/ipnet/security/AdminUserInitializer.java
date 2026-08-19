package com.ipnet.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.model.Role;
import com.ipnet.security.model.User;
import com.ipnet.security.enums.UserRole;
import com.ipnet.security.repository.RoleRepository;
import com.ipnet.security.repository.UserRepository;

import java.util.Set;
import java.util.UUID;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final String ADMIN_TELEPHONE = "+261340000000";
    private static final String ADMIN_PASSWORD = "2468";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // 1. Création automatique de TOUS les rôles au démarrage du serveur s'ils n'existent pas
        for (UserRole roleEnum : UserRole.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role();
                role.setName(roleEnum);
                role.setPublicId(UUID.randomUUID());
                roleRepository.save(role);
                System.out.println(">>> Rôle créé automatiquement : " + roleEnum);
            }
        }

        // 2. Création de l'Administrateur par défaut si non existant
        if (userRepository.existsByTelephone(ADMIN_TELEPHONE)) {
            System.out.println(">>> Admin déjà existant.");
            return;
        }

        Role superAdminRole = roleRepository.findByName(UserRole.SUPER_ADMIN)
                .orElseThrow(() -> new RuntimeException("Rôle SUPER_ADMIN introuvable"));

        User admin = new User();
        admin.setNom("Administrateur");
        admin.setTelephone(ADMIN_TELEPHONE);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRoles(Set.of(superAdminRole));
        admin.setStatutCompte(StatutCompte.ACTIF);
        admin.setPublicId(UUID.randomUUID());

        userRepository.save(admin);

        System.out.println(">>> Admin créé avec succès (telephone: " + ADMIN_TELEPHONE + " / password: " + ADMIN_PASSWORD + ")");
    }
}
