package com.ipnet.services.implement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipnet.dto.*;
import com.ipnet.entity.*;
import com.ipnet.repository.*;
import com.ipnet.security.model.User;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.mappers.FeedbackMapper;
import com.ipnet.services.interfaces.FeedbackServiceInterface;

@Service
public class FeedbackServiceImpl implements FeedbackServiceInterface {

    @Autowired private FeedbackRepository feedbackRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TrajetRepository trajetRepository;
    @Autowired private FeedbackMapper feedbackMapper;

    @Override
    @Transactional
    public FeedbackResponseDto create(FeedbackRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        TrajetEntity trajet = trajetRepository.findById(dto.getTrajetId())
            .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));

        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setNote(dto.getNote());
        feedback.setCommentaire(dto.getCommentaire());
        feedback.setDateCreation(LocalDateTime.now());
        feedback.setUser(user);
        feedback.setTrajet(trajet);

        FeedbackEntity saved = feedbackRepository.save(feedback);
        return feedbackMapper.toDto(saved);
    }

    @Override
    public List<FeedbackResponseDto> getByTrajet(Long trajetId) {
        return feedbackRepository.findByTrajetId(trajetId)
            .stream()
            .map(feedbackMapper::toDto)
            .collect(Collectors.toList());
    }
}