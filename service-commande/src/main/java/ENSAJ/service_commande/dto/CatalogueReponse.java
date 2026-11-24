package ENSAJ.service_commande.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CatalogueReponse {
    private String skuCode;
    private Boolean estEnStock;
}
