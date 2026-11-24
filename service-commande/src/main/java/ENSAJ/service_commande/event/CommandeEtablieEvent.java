package ENSAJ.service_commande.event;

public record CommandeEtablieEvent(String numCommande, String nom, String prenom, String email) {}
