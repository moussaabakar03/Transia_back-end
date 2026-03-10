package com.ipnet.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.ipnet.security.UserDetailsImpl;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;

public class SecurityUtils {

    private SecurityUtils() {}

    public static User getConnectedUser(UserRepository userRepository) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetailsImpl)) {
            throw new RuntimeException("Principal invalide");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        return userRepository.findByPublicId(userDetails.getId())
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable"));
    }
    
    // 🔹 Vérifier le rôle
    public static boolean hasRole(String role) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_" + role));
    }
}
