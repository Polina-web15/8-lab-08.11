package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.Bot.TelegramMessageSender;
import com.example.demo.repository.TelegramUserRepository;

@Service
public class TelegramNotificationService {
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramMessageSender messageSender;
     public TelegramNotificationService(
            TelegramUserRepository telegramUserRepository,
            TelegramMessageSender messageSender) { // ← внедряем sender
        this.telegramUserRepository = telegramUserRepository;
        this.messageSender = messageSender;
    }

    public void sendTelegramNotification(Long studentId, String message) {
        telegramUserRepository.findByStudentId(studentId)
            .ifPresent(telegramUser -> {
                messageSender.sendMessage(telegramUser.getChatId(), message);
            });
    }
}
