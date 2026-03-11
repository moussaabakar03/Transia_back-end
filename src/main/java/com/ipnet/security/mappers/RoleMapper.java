package com.ipnet.security.mappers;

import com.ipnet.security.model.Role;
import org.springframework.stereotype.Component;

import com.ipnet.security.dto.RoleDTO;
import com.ipnet.security.enums.UserRole;

@Component
public class RoleMapper {

    public Role mapToRole(RoleDTO dto) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(UserRole.valueOf(dto.getName()));
        return role;
    }

    public RoleDTO mapToRoleDTO(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getName().name()
        );
    }
}
