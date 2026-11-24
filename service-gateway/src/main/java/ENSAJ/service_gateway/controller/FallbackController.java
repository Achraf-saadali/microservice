package ENSAJ.service_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Route de repli appelée par le Circuit Breaker (forward:/fallbackRoute).
 * @RequestMapping sans méthode : la requête d'origine peut être un GET ou un POST,
 * le forward conserve la méthode HTTP.
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallbackRoute")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service indisponible");
    }
}
