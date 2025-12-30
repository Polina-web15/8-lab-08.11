package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class DeadlinePredictionService {
    private final Map<Long, List<DeadlinePredictionDTO>> deadlines = new ConcurrentHashMap<>();

    public void addDeadline(DeadlinePredictionDTO subject) {
        if (subject.getStudentId() == null) {
            throw new IllegalArgumentException("studentId is required");
        }
        deadlines.computeIfAbsent(subject.getStudentId(), k -> new java.util.ArrayList<>()).add(subject);
    }

    /**
     * Получить все дедлайны студента.
     */
    public List<DeadlinePredictionDTO> getPredictionsForStudent(Long studentId) {
        return deadlines.getOrDefault(studentId, new java.util.ArrayList<>());
    }


    public List<DeadlinePredictionDTO> findUrgentDeadlines(Long studentId, double maxHours) {
        return getPredictionsForStudent(studentId).stream()
            .filter(dto -> {
                // Пересчитываем актуальное hoursLeft
                if (dto.getDeadline() == null || dto.getDeadline().isBefore(LocalDateTime.now())) {
                    return false;
                }
                double hoursLeft = java.time.Duration.between(LocalDateTime.now(), dto.getDeadline()).toHours() +
                                   (java.time.Duration.between(LocalDateTime.now(), dto.getDeadline()).toMinutesPart() / 60.0);
                return hoursLeft <= maxHours;
            })
            .collect(Collectors.toList());
    }
    public List<DeadlinePredictionDTO> getAllPredictions() {
        return deadlines.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
    }
}
    
