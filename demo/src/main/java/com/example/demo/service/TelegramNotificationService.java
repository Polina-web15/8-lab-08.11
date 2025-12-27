package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.demo.repository.TelegramUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {
     private final TelegramUserRepository telegramUserRepository;
    private final TelegramLongPollingBot telegramBot;

    public void sendTelegramNotification(Long studentId, String message) {
        telegramUserRepository.findByStudentId(studentId)
            .ifPresent(telegramUser -> {
                try {
                    telegramBot.execute(
                        org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder()
                            .chatId(telegramUser.getChatId().toString())
                            .text(message)
                            .build()
                    );
                } catch (TelegramApiException e) {
                    e.printStackTrace(); // Или логируйте через Logger
                }
            });
        }
}
