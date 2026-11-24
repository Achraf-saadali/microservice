package ENSAJ.service_catalogue.repository;

import ENSAJ.service_catalogue.model.Catalogue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogueRepository extends JpaRepository<Catalogue, Long> {
    Optional<Catalogue> findBySkuCode(String skuCode);

    List<Catalogue> findBySkuCodeIn(List<String> skuCodes);
}
