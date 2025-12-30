package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Collection;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.demo.dto.StudentDto;
import com.example.demo.model.TimeEntry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final StudentService studentService;
    private final TimeEntryService timeEntryService;
    public byte[] exportStudentsToExcel() throws IOException {
        Collection<StudentDto> students = studentService.getStudents();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Students");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");


            int rowNum = 1;
            for (StudentDto student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.id());
                row.createCell(1).setCellValue(student.name());
            }

            workbook.write(os);
            return os.toByteArray();
        }
        
    }

    public byte[] exportTimeEntriesToExcel(Long studentId) throws IOException {
        List<TimeEntry> entries = timeEntryService.getEntriesByStudentId(studentId);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Time Entries");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Date");
            headerRow.createCell(2).setCellValue("Hours");
            headerRow.createCell(3).setCellValue("Description");

            int rowNum = 1;
            for (TimeEntry entry : entries) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getId());
                row.createCell(1).setCellValue(entry.getStartTime().toLocalDate().toString());
                row.createCell(2).setCellValue(entry.getHours());
                row.createCell(3).setCellValue(entry.getDescription());
            }

            workbook.write(os);
            return os.toByteArray();
        }

        
    }
}