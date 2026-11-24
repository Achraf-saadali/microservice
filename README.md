# Projet Microservices — TP1 à TP10

Projet Spring Boot 3.3.5 / Java 17 composé de six services : Produit, Catalogue, Commande, Discovery Eureka, Gateway et Notification. Les données et outils d’infrastructure sont fournis dans `infra/docker-compose.yml`.

## Architecture

| Service | Port par défaut | Fonction | TP |
|---|---:|---|---|
| service-produit | 8083 | CRUD MongoDB | TP1–TP2 |
| service-commande | 8081 | Commandes MySQL, appel Catalogue, producteur Kafka | TP3, TP5, TP8–TP9 |
| service-catalogue | 8082 | Stocks MySQL | TP4–TP5 |
| service-discovery | 8761 | Eureka Server | TP6 |
| service-gateway | 8888 | Routage, JWT Keycloak, Circuit Breaker | TP6–TP8 |
| service-notification | 8085 | Consommateur Kafka et email SMTP | TP9 |

## Prérequis

- JDK 17 ou supérieur, Maven 3.9 ou supérieur
- Docker Desktop avec Docker Compose
- Postman (facultatif)

## Démarrage de l’infrastructure

Depuis le dossier `infra` :

```bash
docker compose --profile db up -d
# TP7
docker compose --profile security up -d
# TP9
docker compose --profile kafka up -d
# TP10
docker compose --profile monitoring up -d
```

Les identifiants locaux sont `admin/admin` pour Keycloak et Grafana. Keycloak importe automatiquement le realm `spring-boot-microservices-realm` et le client `spring-cloud-client`.

## Ordre de démarrage des applications

Lancer les classes principales dans l’ordre suivant :

1. `ServiceDiscoveryApplication` — 8761
2. `ServiceCatalogueApplication` — 8082, ou plusieurs instances avec `--server.port=0`
3. `ServiceProduitApplication` — 8083
4. `ServiceCommandeApplication` — 8081
5. `ServiceNotificationApplication` — 8085
6. `ServiceGatewayApplication` — 8888

La Gateway sécurise toutes les routes métier. Pour un appel client, obtenir un token Client Credentials depuis :

```text
POST http://localhost:8181/realms/spring-boot-microservices-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials
client_id=spring-cloud-client
client_secret=spring-cloud-secret
```

Utiliser ensuite `Authorization: Bearer <token>` sur les requêtes Gateway.

## Endpoints principaux

```text
POST   http://localhost:8888/api/produit
GET    http://localhost:8888/api/produit
PUT    http://localhost:8888/api/produit/{id}
DELETE http://localhost:8888/api/produit/{id}

GET    http://localhost:8888/api/catalogue?skuCode=iphone_13&skuCode=iphone_15
POST   http://localhost:8888/api/commande
GET    http://localhost:8888/api/commande
```

Exemple de commande :

```json
{
  "articleLigneCommandeDtoList": [
    { "skucode": "iphone_13", "prix": 500, "quantite": 1 }
  ],
  "nom": "Test",
  "prenom": "Client",
  "email": "test@example.com"
}
```

`iphone_13` est en stock dans les données d’initialisation. `iphone_15` est volontairement en rupture afin de tester le rejet métier. Une panne ou un timeout de Catalogue renvoie `503` via le Circuit Breaker ; une rupture de stock renvoie `400`.

## Tests et vérifications

Compilation de tous les services :

```bash
mvn clean package
```

Tests d’intégration Produit avec Testcontainers :

```bash
mvn -pl service-produit test
```

Cette commande nécessite un moteur Docker actif. Les tests couvrent POST, GET, PUT et DELETE avec une instance MongoDB isolée.

Monitoring :

- Prometheus : http://localhost:9090/targets
- Grafana : http://localhost:3000
- Kafka UI : http://localhost:8086
- Mailpit : http://localhost:8025
- Eureka : http://localhost:8761
- Actuator Gateway : http://localhost:8888/actuator/health

Le dashboard Grafana est provisionné depuis `infra/grafana/provisioning/dashboards/microservices.json`.

## Configuration par variables d’environnement

Les valeurs locales sont utilisées par défaut, mais peuvent être remplacées pour Docker ou un autre environnement : `MONGODB_URI`, `MYSQL_USER`, `MYSQL_PASSWORD`, `EUREKA_URL`, `KAFKA_BOOTSTRAP_SERVERS`, `MAIL_HOST`, `MAIL_PORT`, `KEYCLOAK_ISSUER_URI` et `PORT`.

## Limites d’exécution dans l’environnement de développement

Le code est compilable et les tests sont prêts. L’exécution de l’intégration complète nécessite Docker Desktop pour MongoDB, MySQL, Kafka, Keycloak, Prometheus et Grafana. Les ports des applications Java restent libres pour IntelliJ ou `mvn spring-boot:run`.
