package com.example.demo.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HeatmapData {
    private List<String> days = Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс");
    private List<Integer> hours = IntStream.range(0, 24).boxed().collect(Collectors.toList());
    private int[][] data; // [день][час] = количество минут

    public HeatmapData(Long studentId, List<TimeEntry> entries) {
        this.data = new int[7][24]; // 7 дней, 24 часа

        for (TimeEntry entry : entries) {
            LocalDateTime start = entry.getStartTime();
            int day = start.getDayOfWeek().getValue() - 1; // 1=Пн → 0, 7=Вс → 6
            int hour = start.getHour();

            long minutes = Duration.between(entry.getStartTime(), entry.getEndTime()).toMinutes();
            if (day >= 0 && day < 7 && hour >= 0 && hour < 24) {
                data[day][hour] += minutes;
            }
        }
    }

    
    public List<String> getDays() { return days; }
    public List<Integer> getHours() { return hours; }
    public int[][] getData() { return data; }
}
