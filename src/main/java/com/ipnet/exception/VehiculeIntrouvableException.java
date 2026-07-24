package com.ipnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VehiculeIntrouvableException extends RuntimeException {
    public VehiculeIntrouvableException() {
        super("Véhicule introuvable");
    }

    public VehiculeIntrouvableException(String message) {
        super(message);
    }
}
