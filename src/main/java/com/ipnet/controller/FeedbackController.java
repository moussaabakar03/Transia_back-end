package com.ipnet.controller;

import com.ipnet.dto.*;
import com.ipnet.services.interfaces.FeedbackServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@CrossOrigin("*")
public class FeedbackController {
    @Autowired private FeedbackServiceInterface feedbackService;

    @PostMapping
    public FeedbackResponseDto create(@RequestBody FeedbackRequestDto dto) {
        return feedbackService.create(dto);
    }

    @GetMapping("/trajet/{trajetId}")
    public List<FeedbackResponseDto> getByTrajet(@PathVariable Long trajetId) {
        return feedbackService.getByTrajet(trajetId);
    }
}