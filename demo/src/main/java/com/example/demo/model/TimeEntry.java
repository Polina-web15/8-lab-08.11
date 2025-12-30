package com.example.demo.model;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
//import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "time_entry")
public class TimeEntry {//время прохода
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @Enumerated(EnumType.STRING)
    private TaskType type;
    private String description;//описание
    @Column(name = "start_time")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;
    public LocalDateTime getStartTime() {return start;}

    public LocalDateTime getEndTime() {return end;}

    private boolean isBillable;// учётное время
    public double getHours() {
    if (start == null || end == null) {
        return 0.0;
    }
    return Duration.between(start, end).toMinutes() / 60.0;
}
}
