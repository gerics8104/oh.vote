package hu.oh.vote.exception.szavazas;

import hu.oh.vote.rest.dto.HibaValasz;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.Optional;

@RestControllerAdvice
public class SzavazasExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<HibaValasz> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        String message = Optional.ofNullable(
                ex.getMostSpecificCause().getMessage()
        ).orElse("").toLowerCase(Locale.ROOT);

        if (message.contains("uk_szavazas_idopont")) {
            return badRequest("Erre az időpontra már létezik szavazás.");
        }

        if (message.contains("uk_szavazat_kepviselo")) {
            return badRequest(
                    "Egy képviselő egy szavazáson csak egyszer szavazhat."
            );
        }

        return badRequest("Adatbázis integritási hiba.");
    }



    @ExceptionHandler(SzavazasValidationException.class)
    public ResponseEntity<HibaValasz> handleValidation(
            SzavazasValidationException ex) {

        return ResponseEntity
                .badRequest()
                .body(new HibaValasz(ex.getMessage()));
    }

    @ExceptionHandler(SzavazasNotFoundException.class)
    public ResponseEntity<HibaValasz> handleNotFound(
            SzavazasNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new HibaValasz(ex.getMessage()));
    }

    private ResponseEntity<HibaValasz> badRequest(String message) {
        return ResponseEntity.badRequest()
                .body(new HibaValasz(message));
    }


}
