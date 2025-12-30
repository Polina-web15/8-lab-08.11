package com.example.demo.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DeadlinePredictionDTO;
import com.example.demo.dto.DeadlinePredictionService;
import com.example.demo.model.TelegramUser;
import com.example.demo.repository.TelegramUserRepository;

@Service
public class DeadlineReminderService {
    private final TelegramNotificationService telegramNotificationService;
    private final TelegramUserRepository telegramUserRepository;
    private final DeadlinePredictionService deadlinePredictionService;

    public DeadlineReminderService(
        TelegramNotificationService telegramNotificationService,
        TelegramUserRepository telegramUserRepository,
        DeadlinePredictionService deadlinePredictionService
    ) {
        this.telegramNotificationService = telegramNotificationService;
        this.telegramUserRepository = telegramUserRepository;
        this.deadlinePredictionService = deadlinePredictionService;
    }
    @Scheduled(fixedRate = 60_000) // каждые 5 минут
    public void sendUrgentDeadlineReminders() {
        List<TelegramUser> allUsers = telegramUserRepository.findAll();
        for (TelegramUser user : allUsers) {
            Long studentId = user.getStudentId();

        // Используем новый метод для поиска срочных дедлайнов
            List<DeadlinePredictionDTO> urgent = deadlinePredictionService.findUrgentDeadlines(studentId, 1.0);

            if (!urgent.isEmpty()) {
                telegramNotificationService.sendTelegramNotification(
                studentId,
                " СРОЧНО! У вас есть дедлайн, который заканчивается в течение часа!"
            );}
        }
    }
}
