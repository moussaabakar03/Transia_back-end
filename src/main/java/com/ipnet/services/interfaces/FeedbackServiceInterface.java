package com.ipnet.services.interfaces;

import java.util.List;
import com.ipnet.dto.FeedbackDto;

public interface FeedbackServiceInterface {
    List<FeedbackDto> getAllFeedbacks();
    void saveFeedback(FeedbackDto dto);

    
}