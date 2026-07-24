package com.ipnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SiegeIndisponibleException extends RuntimeException {
    public SiegeIndisponibleException(String message) {
        super(message);
    }
}
