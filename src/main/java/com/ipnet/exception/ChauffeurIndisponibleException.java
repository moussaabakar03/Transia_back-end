package com.ipnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ChauffeurIndisponibleException extends RuntimeException {
    public ChauffeurIndisponibleException() {
        super("Ce chauffeur n'est pas disponible pour ce créneau");
    }
}
