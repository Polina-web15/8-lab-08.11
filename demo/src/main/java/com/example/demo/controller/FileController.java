package com.example.demo.controller;

import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ImportResult;
import com.example.demo.service.FileService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    @PostMapping(value = "/upload", consumes = "multipart/form-data") //загрузка файла
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileService.saveFile(file);
            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Ошибка при загрузке файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importFromCsv(@RequestParam("file") MultipartFile file) {
        ImportResult result = fileService.importFromCsv(file);
        String errorLogFile = null;
        if (!result.errors().isEmpty()) {
            errorLogFile = fileService.writeErrorLogFile(result.errors());
        }

        if (result.errors().isEmpty()) {
            return ResponseEntity.ok("Успешно импортировано студентов: " + result.successCount());
        } else {
            String message = "Импорт завершён с ошибками. Успешно: " + result.successCount() +
                           ". Ошибок: " + result.errors().size() +
                           (errorLogFile != null ? ". Лог ошибок: " + errorLogFile : "");
            return ResponseEntity.badRequest().body(message);
        }
    }
}

