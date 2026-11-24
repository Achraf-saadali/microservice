package ENSAJ.service_commande.controller;

import ENSAJ.service_commande.dto.CommandeRequest;
import ENSAJ.service_commande.exception.CatalogueIndisponibleException;
import ENSAJ.service_commande.model.Commande;
import ENSAJ.service_commande.service.CommandeService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commande")
@RequiredArgsConstructor
public class CommandeController {

    private static final String MSG_REPLI = "Problème de connexion avec le catalogue, veuillez réessayer";

    private final CommandeService commandeService;

    // POST /api/commande -> crée une commande (protégé par le Circuit Breaker "catalogue")
    @PostMapping
    @Retry(name = "catalogue")
    @CircuitBreaker(name = "catalogue", fallbackMethod = "fallbackMethod")
    public ResponseEntity<String> etablirCommande(@RequestBody CommandeRequest commandeRequest) {
        commandeService.etablirCommande(commandeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("la commande est établie avec succès");
    }

    // Repli : catalogue en panne / trop lent. Renvoie un vrai 503 (et non un 201 trompeur).
    public ResponseEntity<String> fallbackMethod(CommandeRequest commandeRequest,
                                                 CatalogueIndisponibleException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(MSG_REPLI);
    }

    // Repli : circuit OUVERT, l'appel n'est même pas tenté
    public ResponseEntity<String> fallbackMethod(CommandeRequest commandeRequest,
                                                 CallNotPermittedException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(MSG_REPLI);
    }

    // GET /api/commande -> liste des commandes (pour vérifier)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Commande> listerCommandes() {
        return commandeService.listerCommandes();
    }
}
