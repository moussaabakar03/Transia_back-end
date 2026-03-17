package com.ipnet.controller;

import com.ipnet.dto.FeedbackDto;
import com.ipnet.services.interfaces.FeedbackServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.ipnet.services.interfaces.FeedbackServiceInterface;

@RestController
@RequestMapping("/api/v1/feedback")
@CrossOrigin("*") 
public class FeedbackController {

    @Autowired
    private FeedbackServiceInterface feedbackService;

    @GetMapping("/all")
        public ResponseEntity<List<FeedbackDto>> getAll() {
            return ResponseEntity.ok(feedbackService.getAllFeedbacks());
        }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody FeedbackDto dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            feedbackService.saveFeedback(dto);
            
            response.put("message", "Merci ! Votre avis et votre commentaire ont été enregistrés avec succès.");
            response.put("status", HttpStatus.OK.value());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            response.put("message", "Erreur lors de l'enregistrement : " + e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}