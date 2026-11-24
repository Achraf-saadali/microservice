package ENSAJ.service_commande.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeRequest {
    private List<ArticleLigneCommandeDto> articleLigneCommandeDtoList;
    private String nom;
    private String prenom;
    private String email;
}
