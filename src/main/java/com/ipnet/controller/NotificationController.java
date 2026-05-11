package com.ipnet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired private NotificationServiceInterface notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getMesNotifications(userId));
    }

    @PatchMapping("/{id}/lire")
    public ResponseEntity<Void> lire(@PathVariable Long id) {
        notificationService.marquerCommeLu(id);
        return ResponseEntity.ok().build();
    }
}