# service-gateway (TP6 + TP8)

Spring Cloud Gateway (port 8888), client Eureka, Circuit Breaker Resilience4j.

| Requête            | Destination                    | Circuit Breaker                  |
|--------------------|--------------------------------|----------------------------------|
| /api/produit/**    | lb://service-produit           | serviceProduitCircuitBreaker     |
| /api/commande/**   | lb://service-commande          | serviceCommandeCircuitBreaker    |
| /api/catalogue/**  | lb://service-catalogue         | serviceCatalogueCircuitBreaker   |
| /  et /eureka/**   | http://localhost:8761 (Eureka) | -                                |

- Repli : GET/POST /fallbackRoute -> 503 "Service indisponible".
- État des circuits : GET http://localhost:8888/actuator/health (ou /actuator/circuitbreakers).

Routes par propriétés : `mvn spring-boot:run -Dspring-boot.run.profiles=props`
