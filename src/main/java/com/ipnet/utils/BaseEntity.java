package com.ipnet.utils;

import java.io.Serializable;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity implements Serializable{

	@CreatedBy
	//@Column(updatable = false)
	private String creerPar;
	
	@LastModifiedBy
	//@Column(name="modifier_par")
	private String modiferPar;

	@CreatedDate
	//@Column(updatable = false)
	private String dateCreation;

	@LastModifiedDate
	private String dateModification;

	public String getCreerPar() {
		return creerPar;
	}

	public void setCreerPar(String creerPar) {
		this.creerPar = creerPar;
	}

	public String getModiferPar() {
		return modiferPar;
	}

	public void setModiferPar(String modiferPar) {
		this.modiferPar = modiferPar;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getDateModification() {
		return dateModification;
	}

	public void setDateModification(String dateModification) {
		this.dateModification = dateModification;
	}
	
	

	
}
