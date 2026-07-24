package com.ipnet.security.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ipnet.entity.AgenceEntity;
import com.ipnet.entity.VilleEntity;
import com.ipnet.repository.AgenceRepository;
import com.ipnet.repository.VilleRepository;
import com.ipnet.security.exception.AlreadyExistException;
import com.ipnet.security.exception.ResourceNotFoundException;
import com.ipnet.security.exception.response.AuthenticationResponse;
import com.ipnet.security.UserDetailsImpl;
import com.ipnet.security.dto.*;
import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.enums.UserRole;
import com.ipnet.security.jwt.JwtUtils;
import com.ipnet.security.mappers.UserMapper;
import com.ipnet.security.model.History;
import com.ipnet.security.model.PasswordResetToken;
import com.ipnet.security.model.Role;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.HistoryRepository;
import com.ipnet.security.repository.PasswordResetTokenRepository;
import com.ipnet.security.repository.ProfilRepository;
import com.ipnet.security.repository.RoleRepository;
import com.ipnet.security.repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final long RESET_TOKEN_VALIDITY_MINUTES = 30;
    private static final int MAX_TENTATIVES_ECHOUEES = 5;
    private static final long DUREE_VERROUILLAGE_MINUTES = 3;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final HistoryRepository historyRepository;
    private final RoleRepository roleRepository;
    private final ProfilRepository profilRepository;
    private final AgenceRepository agenceRepository;
    private final VilleRepository villeRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public UserServiceImpl(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            JwtUtils jwtUtils, UserRepository userRepository, UserMapper userMapper,
            HistoryRepository historyRepository, RoleRepository roleRepository,
            ProfilRepository profilRepository, AgenceRepository agenceRepository,
            VilleRepository villeRepository, PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.historyRepository = historyRepository;
        this.roleRepository = roleRepository;
        this.profilRepository = profilRepository;
        this.agenceRepository = agenceRepository;
        this.villeRepository = villeRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }


    @Override 
    public AuthenticationResponse authenticate(LoginDTO loginDTO) {
        User user = userRepository.findByTelephone(loginDTO.getTelephone()).orElse(null);

        if (user != null && user.getVerrouilleJusqua() != null && user.getVerrouilleJusqua().isAfter(Instant.now())) {
            long minutesRestantes = Math.max(1,
                    (Duration.between(Instant.now(), user.getVerrouilleJusqua()).toSeconds() + 59) / 60);
            throw new IllegalArgumentException("Compte temporairement bloqué suite à plusieurs échecs de connexion. "
                    + "Réessayez dans " + minutesRestantes + " minute(s).");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getTelephone(), loginDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities()
                    .stream().map(item -> item.getAuthority()).collect(Collectors.toList());

            createHistory(userDetails.getId());

            if (user != null && (nonZero(user.getTentativesEchouees()) || user.getVerrouilleJusqua() != null)) {
                user.setTentativesEchouees(0);
                user.setVerrouilleJusqua(null);
                userRepository.save(user);
            }

            AuthenticationResponse response = new AuthenticationResponse(
                    token, userDetails.getId(), userDetails.getFullName(), userDetails.getUsername(), roles);

            if (user != null && user.getAgence() != null) {
                response.setAgenceId(user.getAgence().getId());
                response.setAgenceNom(user.getAgence().getNom());
                if (user.getAgence().getVille() != null) {
                    response.setVilleId(user.getAgence().getVille().getId());
                    response.setVilleNom(user.getAgence().getVille().getNomVille());
                }
            }

            return response;

        } catch (BadCredentialsException ex) {
            if (user != null) {
                boolean vientDetreVerrouille = enregistrerTentativeEchouee(user);
                if (vientDetreVerrouille) {
                    throw new IllegalArgumentException("Trop de tentatives échouées. Compte bloqué temporairement pendant "
                            + DUREE_VERROUILLAGE_MINUTES + " minutes.");
                }
                int tentativesRestantes = MAX_TENTATIVES_ECHOUEES - user.getTentativesEchouees();
                throw new IllegalArgumentException("Les paramètres de connexion sont incorrectes. Il vous reste "
                        + tentativesRestantes + " tentative(s) avant blocage temporaire.");
            }
            throw new IllegalArgumentException("Les paramètres de connexion sont incorrectes");
        } catch (DisabledException ex) {
            throw new IllegalArgumentException("Ce compte est inactif ou a été supprimé");
        } catch (LockedException ex) {
            throw new IllegalArgumentException("Ce compte est bloqué");
        }
    }

    private boolean nonZero(Integer valeur) {
        return valeur != null && valeur != 0;
    }

    /** Incrémente le compteur d'échecs et verrouille temporairement le compte au-delà du seuil. Retourne true si le verrouillage vient d'être déclenché. */
    private boolean enregistrerTentativeEchouee(User user) {
        int tentatives = (user.getTentativesEchouees() == null ? 0 : user.getTentativesEchouees()) + 1;

        if (tentatives >= MAX_TENTATIVES_ECHOUEES) {
            user.setVerrouilleJusqua(Instant.now().plus(DUREE_VERROUILLAGE_MINUTES, ChronoUnit.MINUTES));
            user.setTentativesEchouees(0);
            userRepository.save(user);
            return true;
        }

        user.setTentativesEchouees(tentatives);
        userRepository.save(user);
        return false;
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        if (userRepository.existsByTelephone(userDTO.getTelephone())) {
            throw new AlreadyExistException("Ce numéro de téléphone est déjà utilisé");
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isBlank()
                && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new AlreadyExistException("Cet e-mail est déjà utilisé");
        }

        User user = userMapper.mapToUser(userDTO);
        user.setPublicId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(resolveRoles(userDTO.getRoles()));

        applyVillesEtAgence(user, userDTO);
        user.setStatutOperationnel(userDTO.getStatutOperationnel());

        User savedUser = userRepository.save(user);

        History history = new History();
        history.setName("Enregistrement de l'utilisateur " + savedUser.getNom());
        history.setUser(savedUser);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return userMapper.mapToUserDTO(savedUser);
    }

    @Override
    public List<UserRoleReponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getStatutCompte() != StatutCompte.SUPPRIME)
                .map(userMapper::mapToUserRoleDTO)
                .toList();
    }

    @Override
    public UserRoleReponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User is not exists with given id : "+id));

        return userMapper.mapToUserRoleDTO(user);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, UUID id) {
        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User is not exists with given id:" + id));

        user.setNom(userDTO.getFullName());

        if (userDTO.getTelephone() != null && !userDTO.getTelephone().equals(user.getTelephone())) {
            if (userRepository.existsByTelephone(userDTO.getTelephone())) {
                throw new AlreadyExistException("Ce numéro de téléphone est déjà utilisé");
            }
            user.setTelephone(userDTO.getTelephone());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (!userDTO.getEmail().isBlank() && userRepository.existsByEmail(userDTO.getEmail())) {
                throw new AlreadyExistException("Cet e-mail est déjà utilisé");
            }
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            user.setRoles(resolveRoles(userDTO.getRoles()));
        }

        applyVillesEtAgence(user, userDTO);
        user.setStatutOperationnel(userDTO.getStatutOperationnel());

        History history = new History();
        history.setName("Modification de l'utilisateur " + user.getNom());
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        User updateUser = userRepository.save(user);
        return userMapper.mapToUserDTO(updateUser);
    }

    @Override
    public UserDTO updateMyInfo(UUID id, SelfUpdateDTO dto) {
        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setNom(dto.getFullName());
        }

        if (dto.getTelephone() != null && !dto.getTelephone().isBlank() && !dto.getTelephone().equals(user.getTelephone())) {
            if (userRepository.existsByTelephone(dto.getTelephone())) {
                throw new AlreadyExistException("Ce numéro de téléphone est déjà utilisé");
            }
            user.setTelephone(dto.getTelephone());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (!dto.getEmail().isBlank() && userRepository.existsByEmail(dto.getEmail())) {
                throw new AlreadyExistException("Cet e-mail est déjà utilisé");
            }
            user.setEmail(dto.getEmail().isBlank() ? null : dto.getEmail());
        }

        User updatedUser = userRepository.save(user);

        History history = new History();
        history.setName("Modification de ses informations personnelles par " + user.getNom());
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return userMapper.mapToUserDTO(updatedUser);
    }

    private void applyVillesEtAgence(User user, UserDTO dto) {
        if (dto.getAgenceId() != null) {
            AgenceEntity agence = agenceRepository.findById(dto.getAgenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable : " + dto.getAgenceId()));
            user.setAgence(agence);
        } else {
            user.setAgence(null);
        }
        if (dto.getVilleBaseId() != null) {
            VilleEntity base = villeRepository.findById(dto.getVilleBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ville de base introuvable"));
            user.setVilleBase(base);
        } else {
            user.setVilleBase(null);
        }
        if (dto.getVilleActuelleId() != null) {
            VilleEntity actuelle = villeRepository.findById(dto.getVilleActuelleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ville actuelle introuvable"));
            user.setVilleActuelle(actuelle);
        } else {
            user.setVilleActuelle(null);
        }
    }

    private Set<Role> resolveRoles(Set<UserRole> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("Au moins un rôle doit être attribué à l'utilisateur");
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseGet(() -> {
                            Role role = new Role();
                            role.setName(name);
                            role.setPublicId(UUID.randomUUID());
                            return roleRepository.save(role);
                        }))
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteUserById(UUID id) {
        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable!"));

        user.setStatutCompte(StatutCompte.SUPPRIME);
        userRepository.save(user);

        History history = new History();
        history.setName("Suppression du compte de l'utilisateur " + user.getNom());
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);
    }

    @Override
    public UserDTO changerStatutCompte(UUID id, StatutCompte statutCompte) {
        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        user.setStatutCompte(statutCompte);
        User updatedUser = userRepository.save(user);

        History history = new History();
        history.setName("Changement de statut du compte de " + user.getNom() + " vers " + statutCompte);
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return userMapper.mapToUserDTO(updatedUser);
    }

    @Override
    public UserDTO updatePassword(UUID id, PasswordDTO passwordDTO) {

        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Le mot de passe actuel ne correspond pas !");
        }

        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        User updatedUser = userRepository.save(user);

        History history = new History();
        history.setName("Modification du mot de passe de l'utilisateur " + user.getNom());
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return userMapper.mapToUserDTO(updatedUser);
    }

    @Override
    public String resetPasswordByAdmin(UUID id) {
        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        History history = new History();
        history.setName("Réinitialisation du mot de passe de l'utilisateur " + user.getNom() + " par un administrateur");
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return tempPassword;
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByTelephone(dto.getTelephone())
                .orElseThrow(() -> new ResourceNotFoundException("Aucun compte trouvé avec ce numéro"));

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Aucun e-mail enregistré pour ce compte. Contactez un agent pour réinitialiser votre mot de passe.");
        }

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUser(user);
        resetToken.setDateExpiration(Instant.now().plus(RESET_TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES));
        resetToken.setUtilise(false);
        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken.getToken());
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Lien de réinitialisation invalide"));

        if (resetToken.isUtilise() || resetToken.getDateExpiration().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Ce lien de réinitialisation a expiré ou a déjà été utilisé");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        resetToken.setUtilise(true);
        passwordResetTokenRepository.save(resetToken);

        History history = new History();
        history.setName("Réinitialisation du mot de passe par lien e-mail pour " + user.getNom());
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);
    }

    @Override
    public List<HistoryReponse> getAllHistory() {
        return historyRepository.findAllByOrderByDateHistoryDesc()
                .stream().map(userMapper::mapToHistoryReponse)
                .toList();
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream().map(userMapper::mapToRoleDTO)
                .toList();
    }

    @Override
    public List<UserRoleReponse> getChauffeurs() {
        return userRepository.findAllByRoles_NameAndStatutCompte(UserRole.CHAUFFEUR, StatutCompte.ACTIF)
                .stream()
                .map(userMapper::mapToUserRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleReponse> getLivreurs() {
        return userRepository.findAllByRoles_NameAndStatutCompte(UserRole.LIVREUR, StatutCompte.ACTIF)
                .stream()
                .map(userMapper::mapToUserRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleReponse> getChauffeursByVille(UUID villeId) {
        return userRepository.findAllByRoles_NameAndStatutCompteAndVilleActuelle_Id(
                        UserRole.CHAUFFEUR, StatutCompte.ACTIF, villeId)
                .stream()
                .map(userMapper::mapToUserRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleReponse> getLivreursByVille(UUID villeId) {
        return userRepository.findAllByRoles_NameAndStatutCompteAndVilleActuelle_Id(
                        UserRole.LIVREUR, StatutCompte.ACTIF, villeId)
                .stream()
                .map(userMapper::mapToUserRoleDTO)
                .collect(Collectors.toList());
    }

    private History createHistory(UUID userId) {
        User user = userRepository.findByPublicId(userId).get();
        History history = new History();
        history.setName("Connexion de l'utilisateur " + user.getNom());
        history.setUser(user);
        history.setDateHistory(new Date());
        return historyRepository.save(history);
    }
}
