package com.example.demo.dto;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class DeadlinePredictionDTOController {

    private final DeadlinePredictionService service;

    public DeadlinePredictionDTOController(DeadlinePredictionService service) {
        this.service = service;
    }

    @GetMapping("/subjects")
    public List<DeadlinePredictionDTO> getSubjects() {
        // Для простоты — вернём все дедлайны всех студентов
        return service.getAllPredictions();
    }

    @PostMapping("/subjects")
    public void addSubject(@RequestBody DeadlinePredictionDTO subject) {
        if (subject.getSubject() == null || subject.getDeadline() == null || subject.getStudentId() == null) {
            throw new IllegalArgumentException("Недостаточно данных");
        }
        service.addDeadline(subject);
    }
    @GetMapping("subjects/{studentId}")
    public List<DeadlinePredictionDTO> getByStudent(@PathVariable Long studentId) {
        return service.getPredictionsForStudent(studentId);
    }
}
