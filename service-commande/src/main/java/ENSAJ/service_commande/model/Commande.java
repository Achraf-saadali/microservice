package ENSAJ.service_commande.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "t_commande")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numCommande;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "commande_id")
    private List<ArticleLigneCommande> articleLigneCommande;
}
