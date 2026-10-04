package hu.oh.vote.exception;

import hu.oh.vote.rest.dto.HibaValasz;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HibaValasz> handleValidation(
            MethodArgumentNotValidException ex) {

        String hiba = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage())
                .orElse("Hibás bejövő adatok.");

        return ResponseEntity
                .badRequest()
                .body(new HibaValasz(hiba));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HibaValasz> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        return ResponseEntity
                .badRequest()
                .body(new HibaValasz(
                        "Hibás JSON struktúra vagy mezőérték."
                ));
    }
}