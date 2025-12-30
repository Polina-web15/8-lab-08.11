package com.example.demo.Bot;

import com.example.demo.model.TelegramUser;
import com.example.demo.service.StudentService;
import com.example.demo.service.TelegramUserService;
import com.example.demo.service.TimeEntryService;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot{
    private String botToken;
    private StudentService studentService;
    private TelegramUserService telegramUserService;
    private TimeEntryService timeEntryService;
  

    public TelegramBot(
        @Value("${telegram.bot.token}") String botToken,
            StudentService studentService,
            TelegramUserService telegramUserService,
            TimeEntryService timeEntryService) {
        super(new DefaultBotOptions());
        this.botToken = botToken;
        this.studentService = studentService;
        this.telegramUserService = telegramUserService;
        this.timeEntryService = timeEntryService;
    }
    
    @Override
    public String getBotUsername() {
        return "Notifications_for_study";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            try {
                handleCommand(chatId, messageText);
            } catch (Exception e) {
                sendErrorMessage(chatId, "Произошла ошибка: " + e.getMessage());
            }
        }
    }

    private void handleCommand(Long chatId, String command) {
        switch (command) {
            case "/start":
                sendWelcomeMessage(chatId);
                break;
            case "/time":
                sendTimeReport(chatId);
                break;
            case "/help":
                sendHelpMessage(chatId);                    
                break;
            default:
                sendUnknownCommandMessage(chatId);
        }
    }

    private void sendWelcomeMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Привет! Я бот для учёта времени студентов.\n" +
                "Используйте:\n" +
                "/start — приветствие\n" +
                "/time — отчёт о времени\n" +
                "/help — помощь");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendTimeReport(Long chatId) {
        Optional<TelegramUser> telegramUserOpt = telegramUserService.findByChatId(chatId);
        if (telegramUserOpt.isEmpty()) {
            sendErrorMessage(chatId, "Вы не зарегистрированы");
            return;
        }

        Long studentId = telegramUserOpt.get().getStudentId();
        String report = timeEntryService.getTimeReportForStudent(studentId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(report != null ? report : "Нет данных о затраченном времени.");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendHelpMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Доступные команды:\n" +
                "/start — начать работу\n" +
                "/time — показать отчёт о времени\n" +
                "/help — эта справка");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendUnknownCommandMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Неизвестная команда. Введите /help для списка доступных команд.");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendErrorMessage(Long chatId, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Ошибка: " + errorMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
