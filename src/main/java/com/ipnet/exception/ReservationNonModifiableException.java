package com.ipnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReservationNonModifiableException extends RuntimeException {
    public ReservationNonModifiableException(String message) {
        super(message);
    }
}
