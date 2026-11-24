package ENSAJ.service_commande.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLigneCommandeDto {
    private Long id;
    private String skucode;
    private BigDecimal prix;

    // accepte "quantite" et "quantité" dans le JSON
    @JsonAlias("quantité")
    private Integer quantite;
}
