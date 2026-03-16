package com.ipnet.services.interfaces;

import com.ipnet.dto.FeedbackRequestDto;
import com.ipnet.dto.FeedbackResponseDto;
import java.util.List;

public interface FeedbackServiceInterface {
    FeedbackResponseDto create(FeedbackRequestDto dto);
    List<FeedbackResponseDto> getByTrajet(Long trajetId);
}