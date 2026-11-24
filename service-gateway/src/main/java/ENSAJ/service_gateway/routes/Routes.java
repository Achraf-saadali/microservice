package ENSAJ.service_gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Routes configurées en code Java.
 * Désactivées si le profil "props" est actif (routes alors définies dans application-props.properties).
 * Chaque route métier est protégée par un Circuit Breaker avec repli sur /fallbackRoute.
 */
@Configuration
@Profile("!props")
public class Routes {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("service-produit", r -> r.path("/api/produit/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("serviceProduitCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("lb://service-produit"))
                .route("service-commande", r -> r.path("/api/commande/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("serviceCommandeCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("lb://service-commande"))
                .route("service-catalogue", r -> r.path("/api/catalogue/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("serviceCatalogueCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("lb://service-catalogue"))
                // tableau de bord Eureka via la gateway
                .route("service-discovery", r -> r.path("/", "/eureka/**")
                        .uri("http://localhost:8761"))
                .build();
    }
}
