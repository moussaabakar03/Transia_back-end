package com.ipnet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipnet.security.dto.UserRoleReponse;
import com.ipnet.security.enums.UserRole;
import com.ipnet.security.mappers.UserMapper;
import com.ipnet.security.model.Role;
import com.ipnet.security.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/livreurs")
@CrossOrigin("*")
public class LivreurController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public LivreurController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<UserRoleReponse>> getLivreursDisponibles() {
        List<UserRoleReponse> livreursDto = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .map(Role::getName)
                        .anyMatch(name -> name == UserRole.LIVREUR || name == UserRole.CHAUFFEUR))
                .map(userMapper::mapToUserRoleDTO)
                .toList();

        return ResponseEntity.ok(livreursDto);
    }
}
