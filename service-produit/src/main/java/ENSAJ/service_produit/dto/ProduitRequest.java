package ENSAJ.service_produit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProduitRequest {
    private String id;
    private String libelle;
    private String description;
    private Double prix;
}
