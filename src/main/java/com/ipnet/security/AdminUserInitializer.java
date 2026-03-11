/*package com.ipnet.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.ipnet.security.model.Role;
import com.ipnet.security.model.User;
import com.ipnet.security.enums.*;
import com.ipnet.security.repository.RoleRepository;
import com.ipnet.security.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdminUserInitializer implements CommandLineRunner {

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

      
        Role adminRole = roleRepository.findByName(UserRole.ADMIN)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(UserRole.ADMIN);
                    return roleRepository.save(role);
                });

     
        if (userRepository.existsByUsername("ALI")) {
            System.out.println(">>> Admin déjà existant, aucune action.");
            return;
        }

    
        User admin = new User();
        admin.setNom("Administrateur");
        admin.setUsername("ALI");
        admin.setPassword(passwordEncoder.encode("Moussa"));
        admin.setRole(adminRole);
        admin.setEnable(true);
        admin.setPublicId(UUID.randomUUID());

        userRepository.save(admin);

        System.out.println(">>> Admin créé avec succès (username: Ali / password: Moussa)");
    }
}
*/

