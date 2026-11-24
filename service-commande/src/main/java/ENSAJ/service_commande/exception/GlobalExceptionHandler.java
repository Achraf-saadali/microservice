package ENSAJ.service_commande.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Produit indisponible / requête invalide -> 400 avec un message clair
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erreur", e.getMessage()));
    }

    // Filet de sécurité : en temps normal, le repli du Circuit Breaker répond avant ce handler
    @ExceptionHandler(CatalogueIndisponibleException.class)
    public ResponseEntity<Map<String, String>> handleCatalogueIndisponible(CatalogueIndisponibleException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("erreur", e.getMessage()));
    }
}
