package com.ipnet.security.service;

public interface EmailService {

    void sendPasswordResetEmail(String to, String resetToken);
}
