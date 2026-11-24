package ENSAJ.service_produit.controller;

import ENSAJ.service_produit.dto.ProduitRequest;
import ENSAJ.service_produit.dto.ProduitResponse;
import ENSAJ.service_produit.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produit")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduit(@RequestBody ProduitRequest produitRequest) {
        this.produitService.createProduit(produitRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProduitResponse> getAllProduit() {
        return produitService.getAllProduit();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProduitResponse updateProduit(@PathVariable String id,
                                         @RequestBody ProduitRequest produitRequest) {
        return produitService.updateProduit(id, produitRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduit(@PathVariable String id) {
        produitService.deleteProduit(id);
    }
}
