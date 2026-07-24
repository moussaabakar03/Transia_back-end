package com.ipnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PlacesInsuffisantesException extends RuntimeException {
    public PlacesInsuffisantesException(String message) {
        super(message);
    }
}
