package com.ipnet.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.enums.UserRole;
import com.ipnet.security.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByTelephone(String telephone);
    boolean existsByTelephone(String telephone);

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByPublicId(UUID userId);

    // explication: la requête dérivée findAllByRoles_Name navigue de User vers roles (collection) puis vers name.

	Optional<User> findByNom(String name);

    List<User> findAllByRoles_Name(UserRole roleName);

    List<User> findAllByRoles_NameAndVilleActuelle_Id(UserRole roleName, UUID villeId);

    List<User> findAllByRoles_NameAndStatutCompte(UserRole roleName, StatutCompte statutCompte);

    List<User> findAllByRoles_NameAndStatutCompteAndVilleActuelle_Id(
            UserRole roleName, StatutCompte statutCompte, UUID villeId);

}
