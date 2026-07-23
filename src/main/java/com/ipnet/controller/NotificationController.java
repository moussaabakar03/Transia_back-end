package com.ipnet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipnet.dto.NotificationDto;
import com.ipnet.services.interfaces.NotificationServiceInterface;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin("*")
public class NotificationController {

    private final NotificationServiceInterface notificationService;

    public NotificationController(NotificationServiceInterface notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getMesNotifications(userId));
    }

    @GetMapping("/user/{userId}/non-lues")
    public ResponseEntity<List<NotificationDto>> getNotificationsNonLues(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getMesNotificationsNonLues(userId));
    }

    @GetMapping("/user/{userId}/non-lues/count")
    public ResponseEntity<Map<String, Long>> compterNonLues(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("count", notificationService.compterNonLues(userId)));
    }

    @PatchMapping("/{id}/lire")
    public ResponseEntity<Void> lire(@PathVariable Long id) {
        notificationService.marquerCommeLu(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/{userId}/tout-lire")
    public ResponseEntity<Void> toutLire(@PathVariable Long userId) {
        notificationService.toutMarquerCommeLu(userId);
        return ResponseEntity.noContent().build();
    }
}
