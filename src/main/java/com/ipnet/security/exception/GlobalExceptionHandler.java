package com.ipnet.security.exception;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gère les violations de contraintes SQL :
     * - clé étrangère ;
     * - valeur dupliquée ;
     * - contrainte CHECK ;
     * - valeur trop longue ;
     * - colonne obligatoire non renseignée.
     *
     * La cause technique complète est enregistrée dans la console du backend,
     * tandis qu'un message sécurisé est retourné au client.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request
    ) {
        String causeTechnique = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        LOGGER.error(
                "Violation d'intégrité de la base de données sur {} : {}",
                request.getDescription(false),
                causeTechnique,
                ex
        );

        ErrorMessage response = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                new Date(),
                "Une contrainte de la base de données empêche cette opération.",
                request.getDescription(false)
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    /**
     * Gère les ressources qui existent déjà.
     */
    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorMessage> handleResourceAlreadyExists(
            AlreadyExistException ex,
            WebRequest request
    ) {
        ErrorMessage response = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Gère les arguments invalides envoyés par le client.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request
    ) {
        ErrorMessage response = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Gère les ressources introuvables.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request
    ) {
        ErrorMessage response = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Gère les accès refusés.
     *
     * HTTP 403 est utilisé lorsqu'un utilisateur est authentifié,
     * mais ne possède pas les autorisations nécessaires.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request
    ) {
        ErrorMessage response = new ErrorMessage(
                HttpStatus.FORBIDDEN.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    /**
     * Gère toutes les autres exceptions non prises en charge.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleAllException(
            Exception ex,
            WebRequest request
    ) {
        LOGGER.error(
                "Erreur interne non gérée sur {} : {}",
                request.getDescription(false),
                ex.getMessage(),
                ex
        );

        ErrorMessage response = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                "Une erreur interne est survenue.",
                request.getDescription(false)
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}