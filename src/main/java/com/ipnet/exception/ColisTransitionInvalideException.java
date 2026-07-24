package com.ipnet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ColisTransitionInvalideException extends RuntimeException {
    public ColisTransitionInvalideException(String message) {
        super(message);
    }
}
