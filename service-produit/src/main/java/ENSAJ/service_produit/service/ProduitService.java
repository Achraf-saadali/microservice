package ENSAJ.service_produit.service;

import ENSAJ.service_produit.dto.ProduitRequest;
import ENSAJ.service_produit.dto.ProduitResponse;
import ENSAJ.service_produit.model.Produit;
import ENSAJ.service_produit.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduitService {

    private final ProduitRepository produitRepository;

    // TP8 : temporisation (en ms) pour simuler une réponse tardive de l'API produit.
    // 0 = désactivée. Pour le test du timeout : produit.simulation.delay-ms=5000
    @Value("${produit.simulation.delay-ms:0}")
    private long delayMs;

    public void createProduit(ProduitRequest produitRequest) {
        Produit produit = Produit.builder()
                .libelle(produitRequest.getLibelle())
                .prix(produitRequest.getPrix() == null ? 0 : produitRequest.getPrix())
                .description(produitRequest.getDescription())
                .build();
        produitRepository.save(produit);
        log.info("le Produit {} est enregistré", produit.getId());
    }

    public List<ProduitResponse> getAllProduit() {
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);   // ex. 5000 ms
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        List<Produit> produits = this.produitRepository.findAll();
        return produits.stream().map(this::mapToProduitResponse).toList();
    }

    public ProduitResponse updateProduit(String id, ProduitRequest produitRequest) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produit introuvable : " + id));

        produit.setLibelle(produitRequest.getLibelle());
        produit.setDescription(produitRequest.getDescription());
        if (produitRequest.getPrix() != null) {
            produit.setPrix(produitRequest.getPrix());
        }

        produitRepository.save(produit);
        log.info("le Produit {} est modifié", id);
        return mapToProduitResponse(produit);
    }

    public void deleteProduit(String id) {
        if (!produitRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produit introuvable : " + id);
        }
        produitRepository.deleteById(id);
        log.info("le Produit {} est supprimé", id);
    }

    private ProduitResponse mapToProduitResponse(Produit produit) {
        return ProduitResponse.builder()
                .id(produit.getId())
                .libelle(produit.getLibelle())
                .prix(produit.getPrix())
                .description(produit.getDescription())
                .build();
    }
}
