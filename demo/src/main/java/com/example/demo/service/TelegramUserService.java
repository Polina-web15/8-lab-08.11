package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.Student;
import com.example.demo.model.TelegramUser;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TelegramUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TelegramUserService {
    private final TelegramUserRepository telegramUserRepository;
    private final StudentRepository studentRepository;
    public TelegramUser registerStudent(Long chatId, String username) {
        // Найти студента по username (или другому полю)
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Студент не найден: " + username));

        if (telegramUserRepository.existsByChatId(chatId)) {
            throw new RuntimeException("Этот чат уже зарегистрирован");
        }

        TelegramUser user = TelegramUser.builder()
                .studentId(student.getId())
                .chatId(chatId)
                .isActive(true)
                .build();

        return telegramUserRepository.save(user);
    }

    public Long getStudentIdByChatId(Long chatId) {
        return telegramUserRepository.findByChatId(chatId)
                .map(TelegramUser::getStudentId)
                .orElse(null);
    }
    public Optional<TelegramUser> findByChatId(Long chatId) {
        return telegramUserRepository.findByChatId(chatId);
    }
}
