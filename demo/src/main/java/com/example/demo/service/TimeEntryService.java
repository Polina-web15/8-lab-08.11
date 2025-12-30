package com.example.demo.service;

import java.util.List;


import org.springframework.stereotype.Service;

import com.example.demo.model.TimeEntry;
import com.example.demo.repository.TimeEntryRepository;

import java.time.Duration;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional
public class TimeEntryService {
    
    private final TimeEntryRepository timeEntryRepository;
    private final TelegramNotificationService telegramNotificationService;

    public TimeEntryService(TimeEntryRepository timeEntryRepository, TelegramNotificationService telegramNotificationService) {
        this.timeEntryRepository = timeEntryRepository;
        this.telegramNotificationService = telegramNotificationService;
    }
    // Получение всех записей
    public List<TimeEntry> getAll() {
        log.debug("Найдено {} записей времени", timeEntryRepository.findAll());
        return timeEntryRepository.findAll();
    }
    // Сохранение записи
    public TimeEntry save(TimeEntry timeEntry) {
        try{
            validateTimeEntry(timeEntry);
            long durationMinutes = 0;
            if (timeEntry.getStart() != null && timeEntry.getEnd() != null) {
                durationMinutes = java.time.Duration.between(
                timeEntry.getStart(), 
                timeEntry.getEnd()
            ).toMinutes();
            }

            log.info("Запись времени успешно сохранена: id={}, studentId={}, duration={} мин");
            TimeEntry savedEntry = timeEntryRepository.save(timeEntry);

        
            Long studentId = savedEntry.getStudent().getId();
            String message = "✅ Запись времени сохранена!\n" +
                "Тип: " + savedEntry.getType() + "\n" +
                "Длительность: " + durationMinutes + " мин";

            telegramNotificationService.sendTelegramNotification(studentId, message);
            return savedEntry;
        }
        catch (IllegalArgumentException e) {
            log.warn("Ошибка валидации при сохранении записи времени: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении записи времени", e);
            throw e;
        }
    }

    // Получение по ID
    public TimeEntry getById(Long id) {
        log.debug("Поиск записи времени по id={}", id);
        return timeEntryRepository.findById(id).orElse(null);
    }
    // Удаление записи
    public void deleteById(Long id) {
        log.info("Удаление записи времени с id={}", id);
        timeEntryRepository.deleteById(id);
    }

    // Расчёт продолжительности в минутах
    public long getDurationInMinutes(TimeEntry timeEntry) {
        if (timeEntry.getStart() == null || timeEntry.getEnd() == null) {
            log.debug("Невозможно рассчитать длительность: отсутствуют start/end");
            throw new IllegalArgumentException("Start and end times must be set to calculate duration.");
        }
        log.trace("Рассчитана длительность: {} мин (start={}, end={})", timeEntry.getStart(), timeEntry.getEnd());
        return Duration.between(timeEntry.getStart(), timeEntry.getEnd()).toMinutes();
    }

    // Проверка корректности данных
    private void validateTimeEntry(TimeEntry timeEntry) {
        if (timeEntry.getStart() == null || timeEntry.getEnd() == null) {
            log.debug("Ошибка валидации: отсутствуют временные метки");
            throw new IllegalArgumentException("Start and end times are required.");
        }
        if (!timeEntry.getEnd().isAfter(timeEntry.getStart())) {
            log.debug("Ошибка валидации: end <= start (start={}, end={})",
                timeEntry.getStart(), timeEntry.getEnd());
            throw new IllegalArgumentException("End time must be after start time.");
        }
        log.trace("Валидация записи времени пройдена успешно");
    }
    public TimeEntry update(Long id, TimeEntry timeEntry){
        log.info("Обновление записи времени с id={}", id);
         return timeEntryRepository.findById(id).map(existingTimeEntry -> {
            existingTimeEntry.setStudent(timeEntry.getStudent());
            existingTimeEntry.setDescription(timeEntry.getDescription());
            log.info("Запись времени успешно обновлена");
            return timeEntryRepository.save(existingTimeEntry);
        }).orElseGet(() ->{
            log.warn("Попытка обновления несуществующей записи времени с id={}", id);
            return null;
        });
    }

    public String getTimeReportForStudent(Long studentId) {
        List<TimeEntry> entries = timeEntryRepository.findByStudentId(studentId);
        if (entries.isEmpty()) {
            return "Нет данных о затраченном времени.";
        }
        StringBuilder report = new StringBuilder("Ваш отчёт о времени:\n");
        for (TimeEntry entry : entries) {
            long durationMinutes = getDurationInMinutes(entry); // Используем существующий метод
            report.append("- ").append(entry.getType())
              .append(": ").append(durationMinutes).append(" мин\n");
        }
        return report.toString();
    }

    public List<TimeEntry> getEntriesByStudentId(Long studentId) {
        return timeEntryRepository.findByStudentId(studentId);
    }
}
