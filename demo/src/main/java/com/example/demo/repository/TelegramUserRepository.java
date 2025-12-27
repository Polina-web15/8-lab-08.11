package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.TelegramUser;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long>{
     Optional<TelegramUser> findByChatId(Long chatId);

    Optional<TelegramUser> findByStudentId(Long studentId);

    boolean existsByChatId(Long chatId);
}
