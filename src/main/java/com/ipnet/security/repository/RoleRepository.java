package com.ipnet.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipnet.security.enums.UserRole;
import com.ipnet.security.model.Role;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	
	    Optional<Role> findByName(UserRole name);
	    

	    Optional<Role> findByPublicId(UUID publicId);

	    boolean existsByName(UserRole name);
	

}
