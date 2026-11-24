package ENSAJ.service_catalogue.controller;

import ENSAJ.service_catalogue.dto.CatalogueReponse;
import ENSAJ.service_catalogue.model.Catalogue;
import ENSAJ.service_catalogue.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/catalogue")
public class CatalogueController {
    private final CatalogueService catalogueService;


    @Value("${catalogue.simulation.delay-ms:0}")
    private long delayMs;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CatalogueReponse> estEnStocke(@RequestParam List<String> skuCode) {
        simulerLenteur();
        return catalogueService.estEnStocke(skuCode);
    }


    @GetMapping("/produits")
    @ResponseStatus(HttpStatus.OK)
    public List<Catalogue> listerProduits() {
        return catalogueService.listerProduits();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CatalogueReponse> estEnStockePost(@RequestBody List<String> skuCodes) {
        simulerLenteur();
        return catalogueService.estEnStocke(skuCodes);
    }

    private void simulerLenteur() {
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
