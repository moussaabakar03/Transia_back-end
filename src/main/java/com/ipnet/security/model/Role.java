package com.ipnet.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import com.ipnet.security.enums.UserRole;
import com.ipnet.utils.BaseEntity;

import java.io.Serializable;
import java.util.UUID;


@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements Serializable {

    @Override
	public String toString() {
		return ": " + name ;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, unique = true)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 50)
    private UserRole name;

    public Role() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public UserRole getName() {
        return name;
    }

    public void setName(UserRole name) {
        this.name = name;
    }
}