package ENSAJ.service_produit.repository;

import ENSAJ.service_produit.model.Produit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProduitRepository extends MongoRepository<Produit, String> {
}
