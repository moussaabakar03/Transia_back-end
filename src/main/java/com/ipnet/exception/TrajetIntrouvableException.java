package com.ipnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TrajetIntrouvableException extends RuntimeException {
    public TrajetIntrouvableException() {
        super("Trajet introuvable");
    }
}
