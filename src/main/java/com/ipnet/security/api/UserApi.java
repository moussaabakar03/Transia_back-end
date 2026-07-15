package com.ipnet.security.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.exception.response.AuthenticationResponse;
import com.ipnet.security.exception.response.MessageResponse;
import com.ipnet.security.dto.*;
import com.ipnet.security.enums.UserRole;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.security.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Gestion des utilisateurs", description = "Point d'entrée des utilisateurs")
@RestController
@RequestMapping("/api/v1")
public class UserApi {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserApi(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Authenticate",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Jeton non autorisé/invalide", responseCode = "401"),
                    @ApiResponse(description = "Point d'entré non trouvé", responseCode = "404")
            }
    )
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody @Valid LoginDTO loginDTO) {
        return  ResponseEntity.ok(userService.authenticate(loginDTO));
    }

    @PostMapping("/register")
    @Operation(
            description = "Inscription publique — réservée au rôle CLIENT",
            summary = "Auto-inscription d'un client"
    )
    public ResponseEntity<UserDTO> register(@RequestBody RegisterDTO registerDTO) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName(registerDTO.getNom());
        userDTO.setTelephone(registerDTO.getTelephone());
        userDTO.setEmail(registerDTO.getEmail());
        userDTO.setPassword(registerDTO.getPassword());
        userDTO.setRoles(Set.of(UserRole.CLIENT));
        return new ResponseEntity<>(userService.saveUser(userDTO), HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Demande de réinitialisation du mot de passe (lien envoyé par e-mail)")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody ForgotPasswordRequestDTO dto) {
        userService.forgotPassword(dto);
        return ResponseEntity.ok(new MessageResponse(true,
                "Si un e-mail est associé à ce compte, un lien de réinitialisation a été envoyé."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Réinitialisation du mot de passe via le lien reçu par e-mail")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(dto);
        return ResponseEntity.ok(new MessageResponse(true, "Mot de passe réinitialisé avec succès."));
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Modifier son propre mot de passe (utilisateur connecté)")
    public ResponseEntity<UserDTO> updateMyPassword(@RequestBody PasswordDTO passwordDTO) {
        User connectedUser = SecurityUtils.getConnectedUser(userRepository);
        return ResponseEntity.ok(userService.updatePassword(connectedUser.getPublicId(), passwordDTO));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulter mon profil (nom, téléphone, e-mail, rôles...)")
    public ResponseEntity<UserRoleReponse> getMe() {
        User connectedUser = SecurityUtils.getConnectedUser(userRepository);
        return ResponseEntity.ok(userService.getUserById(connectedUser.getId()));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Modifier mes informations personnelles (nom, téléphone, e-mail)")
    public ResponseEntity<UserDTO> updateMe(@RequestBody SelfUpdateDTO dto) {
        User connectedUser = SecurityUtils.getConnectedUser(userRepository);
        return ResponseEntity.ok(userService.updateMyInfo(connectedUser.getPublicId(), dto));
    }

    @GetMapping("/role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Liste des roles")
    public ResponseEntity<List<RoleDTO>> getAllRole() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Créer un compte staff (agent, chauffeur, livreur, admin d'agence...)")
    public ResponseEntity<UserDTO> saveUsers(@RequestBody UserDTO userDTO) {
        enforceAgenceScopeOnWrite(userDTO);
        return new ResponseEntity<>(userService.saveUser(userDTO), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Retrieve all user")
    public ResponseEntity<List<UserRoleReponse>> getAllUser() {
        List<UserRoleReponse> users = userService.getAllUsers();
        if (!SecurityUtils.hasRole("SUPER_ADMIN")) {
            UUID callerAgenceId = callerAgenceId();
            users = users.stream()
                    .filter(u -> callerAgenceId != null && callerAgenceId.equals(u.getAgenceId()))
                    .toList();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Retrieve a user by Id")
    public ResponseEntity<UserRoleReponse> getUserById(@PathVariable("id") Long id) {
        UserRoleReponse user = userService.getUserById(id);
        checkAgenceAccess(user.getAgenceId());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Update a user by Id")
    public ResponseEntity<UserDTO> updateUsers(@RequestBody UserDTO userDTO, @PathVariable("id") UUID id) {
        checkAgenceAccess(userService.getUserById(resolveInternalId(id)).getAgenceId());
        enforceAgenceScopeOnWrite(userDTO);
        return ResponseEntity.ok(userService.updateUser(userDTO, id));
    }

    @PutMapping("/users/{id}/statut")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Activer / désactiver / bloquer un compte")
    public ResponseEntity<UserDTO> changerStatutCompte(@PathVariable("id") UUID id,
                                                         @RequestBody StatutCompteDTO dto) {
        checkAgenceAccess(userService.getUserById(resolveInternalId(id)).getAgenceId());
        return ResponseEntity.ok(userService.changerStatutCompte(id, dto.getStatutCompte()));
    }

    @PutMapping("/users/{id}/reset-password")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Réinitialiser le mot de passe d'un utilisateur (génère un mot de passe temporaire)")
    public ResponseEntity<MessageResponse> resetPasswordByAdmin(@PathVariable("id") UUID id) {
        checkAgenceAccess(userService.getUserById(resolveInternalId(id)).getAgenceId());
        String tempPassword = userService.resetPasswordByAdmin(id);
        return ResponseEntity.ok(new MessageResponse(true, tempPassword));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "History  all")
    public ResponseEntity<List<HistoryReponse>> getAllHistory() {
        return ResponseEntity.ok(userService.getAllHistory());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE')")
    @Operation(summary = "Delete a user by Id")
    public ResponseEntity<Void>  deleteUserById(@PathVariable("id") UUID id) {
        checkAgenceAccess(userService.getUserById(resolveInternalId(id)).getAgenceId());
        this.userService.deleteUserById(id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/utilisateur/chauffeurs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public ResponseEntity<List<UserRoleReponse>> getChauffeurs(@RequestParam(required = false) UUID villeId) {
        if (villeId != null) {
            return ResponseEntity.ok(userService.getChauffeursByVille(villeId));
        }
        return ResponseEntity.ok(userService.getChauffeurs());
    }

    @GetMapping("/utilisateur/livreurs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_AGENCE','AGENT_ACCUEIL')")
    public ResponseEntity<List<UserRoleReponse>> getLivreurs(@RequestParam(required = false) UUID villeId) {
        if (villeId != null) {
            return ResponseEntity.ok(userService.getLivreursByVille(villeId));
        }
        return ResponseEntity.ok(userService.getLivreurs());
    }

    private UUID callerAgenceId() {
        User caller = SecurityUtils.getConnectedUser(userRepository);
        return caller.getAgence() != null ? caller.getAgence().getId() : null;
    }

    private Long resolveInternalId(UUID publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AccessDeniedException("Utilisateur introuvable"))
                .getId();
    }

    private void checkAgenceAccess(UUID targetAgenceId) {
        if (!SecurityUtils.hasRole("SUPER_ADMIN")) {
            UUID callerAgenceId = callerAgenceId();
            if (callerAgenceId == null || !callerAgenceId.equals(targetAgenceId)) {
                throw new AccessDeniedException("Vous ne pouvez pas gérer un utilisateur d'une autre agence");
            }
        }
    }

    private void enforceAgenceScopeOnWrite(UserDTO userDTO) {
        if (!SecurityUtils.hasRole("SUPER_ADMIN")) {
            UUID callerAgenceId = callerAgenceId();
            if (callerAgenceId == null) {
                throw new AccessDeniedException("Aucune agence associée à ce compte administrateur");
            }
            userDTO.setAgenceId(callerAgenceId);

            if (userDTO.getRoles() != null && userDTO.getRoles().stream()
                    .anyMatch(r -> r == UserRole.SUPER_ADMIN || r == UserRole.ADMIN_AGENCE)) {
                throw new AccessDeniedException("Un administrateur d'agence ne peut pas attribuer ce rôle");
            }
        }
    }
}
