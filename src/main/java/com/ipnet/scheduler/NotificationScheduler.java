package com.ipnet.scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.entity.Reservation;
import com.ipnet.entity.TrajetEntity;
import com.ipnet.enums.StatutReservation;
import com.ipnet.enums.TypeNotification;
import com.ipnet.repository.ReservationRepository;
import com.ipnet.services.interfaces.NotificationServiceInterface;

@Component
public class NotificationScheduler {

    private final ReservationRepository reservationRepository;
    private final NotificationServiceInterface notificationService;

    public NotificationScheduler(
        ReservationRepository reservationRepository,
        NotificationServiceInterface notificationService
    ) {
        this.reservationRepository = reservationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Vérifie les réservations chaque minute.
     * En production, la fréquence peut être réduite à toutes les 5 ou 10 minutes.
     */
    @Scheduled(fixedDelayString = "${transia.notifications.check-delay-ms:60000}")
    @Transactional
    public void genererRappelsAutomatiques() {
        List<Reservation> reservations = reservationRepository.findByStatutIn(
            List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE)
        );

        LocalDateTime maintenant = LocalDateTime.now();

        for (Reservation reservation : reservations) {
            traiterReservation(reservation, maintenant);
        }
    }

    private void traiterReservation(Reservation reservation, LocalDateTime maintenant) {
        if (reservation.getUser() == null || reservation.getTrajet() == null) {
            return;
        }

        TrajetEntity trajet = reservation.getTrajet();
        if (trajet.getDateDepart() == null || trajet.getHeureDepart() == null) {
            return;
        }

        LocalDateTime depart = LocalDateTime.of(trajet.getDateDepart(), trajet.getHeureDepart());
        if (!depart.isAfter(maintenant)) {
            return;
        }

        long minutesRestantes = Duration.between(maintenant, depart).toMinutes();
        long heuresRestantes = Math.max(1, (long) Math.ceil(minutesRestantes / 60.0));
        Long userId = reservation.getUser().getId();
        String reference = reservation.getId().toString();
        String trajetLabel = trajet.getVilleDepart().getNomVille()
            + " → "
            + trajet.getVilleArrivee().getNomVille();

        boolean payee = reservation.getPaiement() != null
            || reservation.getStatut() == StatutReservation.CONFIRMEE;

        if (!payee && reservation.getStatut() == StatutReservation.EN_ATTENTE) {
            if (minutesRestantes <= 24 * 60) {
                notificationService.envoyerNotificationUnique(
                    userId,
                    "Paiement urgent",
                    "Votre trajet " + trajetLabel + " commence dans environ "
                        + heuresRestantes
                        + " heure(s). Payez rapidement pour confirmer votre réservation.",
                    TypeNotification.PAIEMENT_URGENT_24H,
                    reference
                );
            } else if (minutesRestantes <= 48 * 60) {
                notificationService.envoyerNotificationUnique(
                    userId,
                    "Rappel de paiement",
                    "Votre réservation pour le trajet " + trajetLabel
                        + " n'est pas encore payée. Le départ est prévu dans moins de 48 heures.",
                    TypeNotification.RAPPEL_PAIEMENT_48H,
                    reference
                );
            }
            return;
        }

        if (payee && minutesRestantes <= 24 * 60) {
            notificationService.envoyerNotificationUnique(
                userId,
                "Rappel de voyage",
                "Votre trajet " + trajetLabel + " commence dans environ "
                    + heuresRestantes
                    + " heure(s). Pensez à préparer votre billet.",
                TypeNotification.RAPPEL_VOYAGE_24H,
                reference
            );
        }
    }
}
