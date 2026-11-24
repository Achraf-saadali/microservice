package ENSAJ.service_catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CatalogueReponse {
    private String skuCode;
    private Boolean estEnStock;
}
