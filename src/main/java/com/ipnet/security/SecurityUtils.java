package com.ipnet.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.ipnet.security.UserDetailsImpl;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;

import java.util.UUID;

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

    /** Agence de l'utilisateur connecté, ou null (SUPER_ADMIN sans agence, ou compte sans agence). */
    public static UUID getConnectedUserAgenceId(UserRepository userRepository) {
        User user = getConnectedUser(userRepository);
        return user.getAgence() != null ? user.getAgence().getId() : null;
    }

    /**
     * Vérifie que l'utilisateur connecté peut agir sur une ressource rattachée à targetAgenceId.
     * SUPER_ADMIN passe toujours ; les autres doivent appartenir exactement à cette agence.
     */
    public static void checkAgenceAccess(UserRepository userRepository, UUID targetAgenceId) {
        if (hasRole("SUPER_ADMIN")) {
            return;
        }
        UUID callerAgenceId = getConnectedUserAgenceId(userRepository);
        if (callerAgenceId == null || !callerAgenceId.equals(targetAgenceId)) {
            throw new AccessDeniedException("Vous ne pouvez pas gérer une ressource d'une autre agence");
        }
    }
}
