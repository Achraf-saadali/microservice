# service-catalogue (TP4 + TP5 + TP6)

Port aléatoire (server.port=0), enregistré dans Eureka sous le nom SERVICE-CATALOGUE.
Base `catalogueDB` (créée automatiquement).

## Lancer plusieurs instances
Ouvrir 2-3 terminaux et lancer dans chacun (démarrer les instances l'une après l'autre) :

    mvn spring-boot:run

(IntelliJ : Edit Configurations > Modify options > Allow multiple instances)

## Endpoints (via la gateway)
- GET http://localhost:8888/api/catalogue?skuCode=iphone_13&skuCode=iphone_15
- GET http://localhost:8888/api/catalogue/produits

## TP8 : simuler un service lent
    mvn spring-boot:run -Dspring-boot.run.arguments=--catalogue.simulation.delay-ms=5000
Chaque appel attend 5 s : service-commande (timeout 2 s) déclenche son repli.
