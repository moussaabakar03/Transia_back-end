package com.ipnet.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    // TODO(package "Services externes") : remplacer par un envoi SMTP/API réel.
    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        logger.info("[STUB email] Réinitialisation de mot de passe pour {} -> token={}", to, resetToken);
    }
}
