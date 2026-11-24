package ENSAJ.service_commande.service;

import ENSAJ.service_commande.dto.ArticleLigneCommandeDto;
import ENSAJ.service_commande.dto.CatalogueReponse;
import ENSAJ.service_commande.dto.CommandeRequest;
import ENSAJ.service_commande.event.CommandeEtablieEvent;
import ENSAJ.service_commande.exception.CatalogueIndisponibleException;
import ENSAJ.service_commande.model.ArticleLigneCommande;
import ENSAJ.service_commande.model.Commande;
import ENSAJ.service_commande.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeService {
    public static final String TOPIC = "commande_etablie";
    private final CommandeRepository commandeRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, CommandeEtablieEvent> kafkaTemplate;
    @Value("${catalogue.timeout-seconds:2}") private long timeoutSeconds;

    @Transactional
    public void etablirCommande(CommandeRequest request) {
        if (request == null || request.getArticleLigneCommandeDtoList() == null || request.getArticleLigneCommandeDtoList().isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins un article");
        }
        Commande commande = new Commande();
        commande.setNumCommande(UUID.randomUUID().toString());
        List<ArticleLigneCommande> articles = request.getArticleLigneCommandeDtoList().stream().map(this::mapDto).toList();
        if (articles.stream().anyMatch(a -> a.getSkucode() == null || a.getSkucode().isBlank())) {
            throw new IllegalArgumentException("Chaque article doit avoir un skucode");
        }
        commande.setArticleLigneCommande(articles);
        List<String> skus = articles.stream().map(ArticleLigneCommande::getSkucode).distinct().toList();
        CatalogueReponse[] responses = appelerCatalogue(skus);
        Set<String> inStock = responses == null ? Set.of() : Arrays.stream(responses)
                .filter(r -> Boolean.TRUE.equals(r.getEstEnStock()))
                .map(CatalogueReponse::getSkuCode).collect(Collectors.toSet());
        List<String> unavailable = skus.stream().filter(sku -> !inStock.contains(sku)).toList();
        if (!unavailable.isEmpty()) {
            throw new IllegalArgumentException("Produit(s) non disponible(s) en stock : " + unavailable);
        }
        commandeRepository.save(commande);
        var event = new CommandeEtablieEvent(commande.getNumCommande(), request.getNom(), request.getPrenom(), request.getEmail());
        kafkaTemplate.send(TOPIC, commande.getNumCommande(), event).whenComplete((result, error) -> {
            if (error != null) log.error("Échec publication Kafka pour {}", commande.getNumCommande(), error);
            else log.info("Événement Kafka publié pour {}", commande.getNumCommande());
        });
    }

    @Transactional(readOnly = true)
    public List<Commande> listerCommandes() { return commandeRepository.findAll(); }

    private ArticleLigneCommande mapDto(ArticleLigneCommandeDto dto) {
        if (dto == null) throw new IllegalArgumentException("Une ligne de commande est obligatoire");
        var article = new ArticleLigneCommande();
        article.setPrix(dto.getPrix()); article.setSkucode(dto.getSkucode()); article.setQuantite(dto.getQuantite());
        return article;
    }

    private CatalogueReponse[] appelerCatalogue(List<String> skus) {
        try {
            return webClientBuilder.build().post().uri("http://service-catalogue/api/catalogue")
                    .bodyValue(skus).retrieve().bodyToMono(CatalogueReponse[].class)
                    .timeout(Duration.ofSeconds(timeoutSeconds)).block();
        } catch (WebClientRequestException | WebClientResponseException e) {
            throw new CatalogueIndisponibleException("service-catalogue injoignable", e);
        } catch (RuntimeException e) {
            if (Exceptions.unwrap(e) instanceof TimeoutException) {
                throw new CatalogueIndisponibleException("service-catalogue : délai dépassé", e);
            }
            throw e;
        }
    }
}
