package com.ipnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TrajetNonProgrammeException extends RuntimeException {
    public TrajetNonProgrammeException() {
        super("Ce trajet ne peut pas être démarré (statut incorrect)");
    }
}
