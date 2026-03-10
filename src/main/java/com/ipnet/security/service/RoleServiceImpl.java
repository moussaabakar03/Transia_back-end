package com.ipnet.security.service;

import com.ipnet.security.dto.RoleDTO;
import com.ipnet.security.enums.UserRole;
import com.ipnet.security.mappers.RoleMapper;
import com.ipnet.security.model.Role;
import com.ipnet.security.repository.RoleRepository;
import com.ipnet.security.service.RoleService;
import com.ipnet.security.model.History;
import com.ipnet.security.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final HistoryRepository historyRepository;

    public RoleServiceImpl(RoleRepository roleRepository,
                           RoleMapper roleMapper,
                           HistoryRepository historyRepository) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.historyRepository = historyRepository;
    }

    /* ================= CREATE ================= */
    @Override
    public RoleDTO saveRole(RoleDTO roleDTO) {

        UserRole roleName = UserRole.valueOf(roleDTO.getName());

        if (roleRepository.existsByName(roleName)) {
            throw new RuntimeException("Ce rôle existe déjà !");
        }

        Role role = roleMapper.mapToRole(roleDTO);

        // Générer le publicId
        role.setPublicId(UUID.randomUUID());

        Role savedRole = roleRepository.save(role);

        // Historique
        History history = new History();
        history.setName("Création du rôle : " + savedRole.getName());
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return roleMapper.mapToRoleDTO(savedRole);
    }

    /* ================= UPDATE ================= */
    @Override
    public RoleDTO updateRole(UUID publicId, RoleDTO roleDTO) {

        Role role = roleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));

        role.setName(UserRole.valueOf(roleDTO.getName()));

        Role updatedRole = roleRepository.save(role);

        History history = new History();
        history.setName("Modification du rôle : " + updatedRole.getName());
        history.setDateHistory(new Date());
        historyRepository.save(history);

        return roleMapper.mapToRoleDTO(updatedRole);
    }

    /* ================= READ ONE ================= */
    @Override
    public RoleDTO getRoleByPublicId(UUID publicId) {

        Role role = roleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));

        return roleMapper.mapToRoleDTO(role);
    }

    /* ================= READ ALL ================= */
    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::mapToRoleDTO)
                .collect(Collectors.toList());
    }

    /* ================= DELETE ================= */
    @Override
    public void deleteRole(UUID publicId) {

        Role role = roleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));

        roleRepository.delete(role);

        History history = new History();
        history.setName("Suppression du rôle : " + role.getName());
        history.setDateHistory(new Date());
        historyRepository.save(history);
    }
}
