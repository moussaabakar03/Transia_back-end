package com.ipnet.services.interfaces;

import java.util.List;

import com.ipnet.dto.NotificationDto;

public interface NotificationServiceInterface {
    void envoyerNotification(Long userId, String titre, String msg);
    List<NotificationDto> getMesNotifications(Long userId);
    void marquerCommeLu(Long notificationId);
}