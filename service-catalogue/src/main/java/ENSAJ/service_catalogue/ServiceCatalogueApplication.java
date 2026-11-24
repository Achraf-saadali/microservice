package ENSAJ.service_catalogue;

import ENSAJ.service_catalogue.model.Catalogue;
import ENSAJ.service_catalogue.repository.CatalogueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootApplication
public class ServiceCatalogueApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCatalogueApplication.class, args);
    }

    // Produits de test, insérés une seule fois (plusieurs instances partagent la même base)
    @Bean
    public CommandLineRunner commandLineRunner(CatalogueRepository catalogueRepository) {
        return args -> {
            creerSiAbsent(catalogueRepository, "iphone_13", 8000.0, 100);
            creerSiAbsent(catalogueRepository, "iphone_15", 12000.0, 0);
        };
    }

    private void creerSiAbsent(CatalogueRepository repo, String sku, Double prix, Integer quantite) {
        if (repo.findBySkuCode(sku).isPresent()) {
            return;
        }
        Catalogue c = new Catalogue();
        c.setSkuCode(sku);
        c.setPrix(prix);
        c.setQuantite(quantite);
        try {
            repo.save(c);
        } catch (DataIntegrityViolationException e) {
            // une autre instance vient de l'insérer : on ignore
        }
    }
}
