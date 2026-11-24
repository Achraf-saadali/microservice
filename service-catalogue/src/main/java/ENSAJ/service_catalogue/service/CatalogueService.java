package ENSAJ.service_catalogue.service;

import ENSAJ.service_catalogue.dto.CatalogueReponse;
import ENSAJ.service_catalogue.model.Catalogue;
import ENSAJ.service_catalogue.repository.CatalogueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CatalogueService {
    private final CatalogueRepository catalogueRepository;

    // Pour chaque skuCode trouvé en base : estEnStock = quantité > 0
    // (un skuCode inconnu n'apparaît pas dans la réponse)
    @Transactional(readOnly = true)
    public List<CatalogueReponse> estEnStocke(List<String> skuCodes) {
        return catalogueRepository.findBySkuCodeIn(skuCodes).stream()
                .map(catalogue -> CatalogueReponse.builder()
                        .skuCode(catalogue.getSkuCode())
                        .estEnStock(catalogue.getQuantite() != null && catalogue.getQuantite() > 0)
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Catalogue> listerProduits() {
        return catalogueRepository.findAll();
    }
}
