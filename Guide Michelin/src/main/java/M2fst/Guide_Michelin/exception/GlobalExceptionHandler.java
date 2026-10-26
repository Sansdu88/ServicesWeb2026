package M2fst.Guide_Michelin.exception;

import M2fst.Guide_Michelin.dto.ErrorDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(EntityNotFoundException e) {
        return this.erreur(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDto> handleRouteNotFound(NoResourceFoundException e) {
        return this.erreur(HttpStatus.NOT_FOUND, "Route introuvaible : " + e.getResourcePath());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(AccessDeniedException e) {
        return this.erreur(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidation(MethodArgumentNotValidException e) {
        List<String> erreurs = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            erreurs.add(fieldError.getField() + " : " + fieldError.getDefaultMessage());
        }
        return this.erreur(HttpStatus.BAD_REQUEST, String.join(", ", erreurs));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleBodyInvalide(HttpMessageNotReadableException e) {
        return this.erreur(HttpStatus.BAD_REQUEST, "Corps de la requête invalide");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleMauvaisType(MethodArgumentTypeMismatchException e) {
        return this.erreur(HttpStatus.BAD_REQUEST, "Paramètre invalide : " + e.getName());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handleParametreManquant(MissingServletRequestParameterException e) {
        return this.erreur(HttpStatus.BAD_REQUEST, "Paramètre manquant : " + e.getParameterName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        e.printStackTrace();
        return this.erreur(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
    }

    private ResponseEntity<ErrorDto> erreur(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorDto(status.value(), message));
    }
}