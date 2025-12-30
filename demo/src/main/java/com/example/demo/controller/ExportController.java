package com.example.demo.controller;
import com.example.demo.service.ExcelExportService;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Slf4j
public class ExportController {

    private final ExcelExportService excelExportService;

    @GetMapping("/students")
    public ResponseEntity<byte[]> exportStudents() throws IOException {
        byte[] excelData = excelExportService.exportStudentsToExcel();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                .body(excelData);
    }

    @GetMapping("/entries/{studentId}")
    public ResponseEntity<byte[]> exportTimeEntries(@PathVariable Long studentId) throws IOException {
        byte[] excelData = excelExportService.exportTimeEntriesToExcel(studentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=time_entries_" + studentId + ".xlsx")
                .body(excelData);
    }
}