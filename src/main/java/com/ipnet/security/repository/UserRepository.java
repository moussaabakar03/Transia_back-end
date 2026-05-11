package com.ipnet.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.security.enums.UserRole;
import com.ipnet.security.model.Role;
import com.ipnet.security.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    Optional<User> findByPublicId(UUID userId);
    
    List<User> findAllByRole(Role chauffeurRole);
    //List<User> findAllByRole_Name(String roleName); 

    // explication: La requête dérivée findAllByRole_Name navigue de User vers role puis vers name.


	Optional<User> findByNom(String name);


    List<User> findAllByRole_Name(UserRole roleName);

}
