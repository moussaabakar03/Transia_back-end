package com.ipnet.services.interfaces;

import java.util.List;

import com.ipnet.dto.NotificationDto;
import com.ipnet.enums.TypeNotification;

public interface NotificationServiceInterface {

    NotificationDto envoyerNotification(Long userId, String titre, String message);

    NotificationDto envoyerNotificationUnique(
        Long userId,
        String titre,
        String message,
        TypeNotification type,
        String referenceMetier
    );

    List<NotificationDto> getMesNotifications(Long userId);

    List<NotificationDto> getMesNotificationsNonLues(Long userId);

    long compterNonLues(Long userId);

    void marquerCommeLu(Long notificationId);

    void toutMarquerCommeLu(Long userId);
}
