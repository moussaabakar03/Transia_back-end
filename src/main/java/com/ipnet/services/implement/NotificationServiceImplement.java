package com.ipnet.services.implement;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.NotificationDto;
import com.ipnet.entity.NotificationEntity;
import com.ipnet.enums.TypeNotification;
import com.ipnet.mappers.NotificationMapper;
import com.ipnet.repository.NotificationRepository;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.services.interfaces.NotificationServiceInterface;

@Service
@Transactional
public class NotificationServiceImplement implements NotificationServiceInterface {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    public NotificationServiceImplement(
        NotificationRepository notificationRepository,
        NotificationMapper notificationMapper,
        UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getMesNotifications(Long userId) {
        verifierUtilisateur(userId);
        return notificationRepository.findByDestinataireIdOrderByDateEnvoiDesc(userId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getMesNotificationsNonLues(Long userId) {
        verifierUtilisateur(userId);
        return notificationRepository.findByDestinataireIdAndLuFalseOrderByDateEnvoiDesc(userId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long compterNonLues(Long userId) {
        verifierUtilisateur(userId);
        return notificationRepository.countByDestinataireIdAndLuFalse(userId);
    }

    @Override
    public NotificationDto envoyerNotification(Long userId, String titre, String message) {
        return creerNotification(
            userId,
            titre,
            message,
            TypeNotification.INFORMATION,
            null
        );
    }

    @Override
    public NotificationDto envoyerNotificationUnique(
        Long userId,
        String titre,
        String message,
        TypeNotification type,
        String referenceMetier
    ) {
        if (referenceMetier != null && !referenceMetier.isBlank()
            && notificationRepository.existsByDestinataireIdAndTypeAndReferenceMetier(
                userId,
                type,
                referenceMetier.trim()
            )) {
            return null;
        }

        return creerNotification(userId, titre, message, type, referenceMetier);
    }

    @Override
    public void marquerCommeLu(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification introuvable avec l'ID : " + notificationId));

        if (!notification.isLu()) {
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void toutMarquerCommeLu(Long userId) {
        verifierUtilisateur(userId);
        List<NotificationEntity> notifications =
            notificationRepository.findByDestinataireIdAndLuFalseOrderByDateEnvoiDesc(userId);

        notifications.forEach(notification -> notification.setLu(true));
        notificationRepository.saveAll(notifications);
    }

    private NotificationDto creerNotification(
        Long userId,
        String titre,
        String message,
        TypeNotification type,
        String referenceMetier
    ) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre de la notification est obligatoire.");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Le message de la notification est obligatoire.");
        }

        User utilisateur = verifierUtilisateur(userId);

        NotificationEntity notification = new NotificationEntity();
        notification.setTitre(titre.trim());
        notification.setMessage(message.trim());
        notification.setLu(false);
        notification.setType(type == null ? TypeNotification.INFORMATION : type);
        notification.setReferenceMetier(
            referenceMetier == null || referenceMetier.isBlank() ? null : referenceMetier.trim()
        );
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setDestinataire(utilisateur);

        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    private User verifierUtilisateur(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'identifiant de l'utilisateur est obligatoire.");
        }

        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + userId));
    }
}
