package ENSAJ.service_notification.service;

import ENSAJ.service_notification.event.CommandeEtablieEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final JavaMailSender mailSender;
    @Value("${notification.from:no-reply@microservices.local}") private String from;

    @KafkaListener(topics = "commande_etablie", groupId = "service-notification")
    public void listen(CommandeEtablieEvent event) {
        if (event.email() == null || event.email().isBlank()) {
            log.warn("Commande {} reçue sans adresse email", event.numCommande());
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(event.email());
            helper.setSubject("Commande établie : " + event.numCommande());
            helper.setText("Bonjour " + safe(event.prenom()) + " " + safe(event.nom()) + ",\n\nVotre commande " + event.numCommande() + " a bien été établie.\n");
            mailSender.send(message);
            log.info("Email envoyé pour la commande {}", event.numCommande());
        } catch (MessagingException e) {
            throw new IllegalStateException("Impossible de préparer l'email de notification", e);
        }
    }
    private String safe(String value) { return value == null ? "" : value; }
}
