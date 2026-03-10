package com.ipnet.security.service;

import com.ipnet.security.dto.RoleDTO;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleDTO saveRole(RoleDTO roleDTO);

    RoleDTO updateRole(UUID publicId, RoleDTO roleDTO);

    RoleDTO getRoleByPublicId(UUID publicId);

    List<RoleDTO> getAllRoles();

    void deleteRole(UUID publicId);
}
