package com.ipnet.services.implement;
import com.ipnet.dto.NotificationDto;
import com.ipnet.entity.NotificationEntity;
import com.ipnet.mappers.*;
import com.ipnet.repository.NotificationRepository;
import com.ipnet.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class NotificationServiceImplement implements NotificationServiceInterface {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private NotificationMapper notificationMapper;

    @Override
    public List<NotificationDto> getMesNotifications(Long userId) {
        return notificationRepository.findByDestinataireIdOrderByDateCreationDesc(userId)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void envoyerNotification(Long userId, String titre, String msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void marquerCommeLu(Long notificationId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}