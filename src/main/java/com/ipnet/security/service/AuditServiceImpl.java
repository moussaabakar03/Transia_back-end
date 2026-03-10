package com.ipnet.security.service;

import com.ipnet.security.model.History;
import com.ipnet.security.repository.HistoryRepository;
import com.ipnet.security.repository.UserRepository;
import com.ipnet.security.SecurityUtils;
import com.ipnet.security.model.User;
import com.ipnet.security.service.HistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    public AuditServiceImpl(HistoryRepository historyRepository,
                            UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<History> getAllHistory() {
        // 🔹 Retourner toutes les actions triées par date
        return historyRepository.findAllByOrderByDateHistoryDesc();
    }
}
