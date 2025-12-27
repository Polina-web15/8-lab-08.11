package com.example.demo.controller;



import org.springframework.web.bind.annotation.RestController;

//import com.example.demo.model.TaskType;
//import com.example.demo.model.RiskLevel;
import com.example.demo.model.TimeEntry;
import com.example.demo.service.TimeEntryService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api")
@RestController
public class TimeEntryController {

    private final TimeEntryService timeEntryService;
     private static final Logger logger = LoggerFactory.getLogger(TimeEntryController.class);
    // Используем конструктор для внедрения зависимости (рекомендуется)
    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }
    @GetMapping("/times")
    public List<TimeEntry> getTimes() {
        logger.debug("Найдено {} записей времени", timeEntryService.getAll().size());
        return timeEntryService.getAll();
    }

    @PutMapping("/times/{id}")
    public ResponseEntity<TimeEntry> edit(@PathVariable long id, @RequestBody TimeEntry timeEntry) {
        try {
            TimeEntry updated = timeEntryService.update(id, timeEntry);
            if (updated != null) {
                logger.info("Запись времени с id={} успешно обновлена", id);
                return ResponseEntity.ok(updated);
            } else {
                logger.warn("Попытка обновить несуществующую запись времени с id={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при обновлении записи времени с id={}", id, e);
            throw e;
        }
    }

    // Получить запись по ID
    @GetMapping("/times/{id}")
    public ResponseEntity<TimeEntry> getTimeEntryById(@PathVariable Long id) {
        TimeEntry entry = timeEntryService.getById(id);
        if(entry != null){
            logger.debug("Найдена запись времени: id={}", id);
            return ResponseEntity.ok(entry);
        }
        else {
            logger.warn("Запись времени с id={} не найдена", id);
            return ResponseEntity.notFound().build();
        }
    }

    // Создать новую запись
    @PostMapping("/times")
    public ResponseEntity<TimeEntry> createTimeEntry(@RequestBody TimeEntry timeEntry) {
        try {
            TimeEntry savedEntry = timeEntryService.save(timeEntry);
            logger.info("Запись времени успешно создана: id={}", savedEntry.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
        } catch (Exception e) {
            logger.error("Ошибка при создании записи времени", e);
            throw e;
        }
    }

    // Удалить запись
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTimeEntry(@PathVariable Long id) {
        try {
            timeEntryService.deleteById(id);
            logger.info("Запись времени с id={} успешно удалена", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении записи времени с id={}", id, e);
            throw e;
        }
    }

    // Дополнительно: получить длительность в минутах
}
    // @PostMapping("/times")
    // public ResponseEntity<TimeEntry> addTime(@RequestBody @Valid TimeEntry time) {
    //     time.setId((long)times.size() + 1);
    //     times.add(time);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(time);
    // }    
 //LocalDateTime start;
    //LocalDateTime end;