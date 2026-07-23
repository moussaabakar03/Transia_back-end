package com.ipnet.security.mappers;


import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipnet.security.model.*;

import com.ipnet.security.dto.HistoryReponse;
import com.ipnet.security.dto.RoleDTO;
import com.ipnet.security.dto.UserDTO;
import com.ipnet.security.dto.UserRoleReponse;
import com.ipnet.security.repository.ProfilRepository;

@Service
public class UserMapper {

    private final RoleMapper roleMapper;
    private final ProfilRepository profilRepository;

    public UserMapper(RoleMapper roleMapper, ProfilRepository profilRepository) {
        this.roleMapper = roleMapper;
        this.profilRepository = profilRepository;
    }

    public User mapToUser(UserDTO userDTO) {
        User user = new User();
        user.setNom(userDTO.getFullName());
        user.setTelephone(userDTO.getTelephone());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getStatutCompte() != null) {
            user.setStatutCompte(userDTO.getStatutCompte());
        }
        return user;
    }

    public  UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setPublicId(user.getPublicId());
        userDTO.setFullName(user.getNom());
        userDTO.setTelephone(user.getTelephone());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setStatutCompte(user.getStatutCompte());

        if (user.getRoles() != null) {
            userDTO.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }
        if (user.getAgence() != null) {
            userDTO.setAgenceId(user.getAgence().getId());
        }
        if (user.getVilleBase() != null) {
            userDTO.setVilleBaseId(user.getVilleBase().getId());
        }
        if (user.getVilleActuelle() != null) {
            userDTO.setVilleActuelleId(user.getVilleActuelle().getId());
        }

        return userDTO;
    }
    public UserRoleReponse mapToUserRoleDTO(User user) {
        UserRoleReponse userRoleReponse = new UserRoleReponse();
        userRoleReponse.setId(user.getId());
        userRoleReponse.setFullName(user.getNom());
        userRoleReponse.setTelephone(user.getTelephone());
        userRoleReponse.setEmail(user.getEmail());
        userRoleReponse.setStatutCompte(user.getStatutCompte());
        userRoleReponse.setPublicId(user.getPublicId());

        if (user.getRoles() != null) {
            Set<RoleDTO> roleDTOs = user.getRoles().stream()
                    .map(roleMapper::mapToRoleDTO)
                    .collect(Collectors.toSet());
            userRoleReponse.setRoles(roleDTOs);
        }

        if (user.getAgence() != null) {
            userRoleReponse.setAgenceId(user.getAgence().getId());
            userRoleReponse.setAgenceNom(user.getAgence().getNom());
            if (user.getAgence().getVille() != null) {
                userRoleReponse.setVilleNom(user.getAgence().getVille().getNomVille());
            }
        }
        if (user.getVilleBase() != null) {
            userRoleReponse.setVilleBaseId(user.getVilleBase().getId());
            userRoleReponse.setVilleBaseNom(user.getVilleBase().getNomVille());
        }
        if (user.getVilleActuelle() != null) {
            userRoleReponse.setVilleActuelleId(user.getVilleActuelle().getId());
            userRoleReponse.setVilleActuelleNom(user.getVilleActuelle().getNomVille());
        }
        userRoleReponse.setStatutOperationnel(user.getStatutOperationnel());

        profilRepository.findByUserId(user.getId())
                .ifPresent(profil -> userRoleReponse.setPhotoProfil(profil.getPhotoProfil()));

        return userRoleReponse;
    }

    public RoleDTO mapToRoleDTO(Role role) {
        return roleMapper.mapToRoleDTO(role);
    }

    public HistoryReponse mapToHistoryReponse(History history) {

        HistoryReponse historyReponse = new HistoryReponse();
        historyReponse.setId(history.getId());
        historyReponse.setFullName(history.getUser().getNom());
        historyReponse.setName(history.getName());
        historyReponse.setDateHistory(history.getDateHistory());

        return historyReponse;
    }
}
