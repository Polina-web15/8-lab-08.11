package com.example.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ImportResult;
import com.example.demo.dto.StudentImportDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {
    @Value("${upload.dir}")
    private String uploadDir; 
    private final StudentService studentService;

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        // 1. Проверка MIME-типа
        String contentType = file.getContentType();
        log.debug("MIME-тип файла: {}", contentType);
        if (!isValidMimeType(contentType)) {
            throw new IllegalArgumentException("Неподдерживаемый тип файла: " + contentType);
        }

        // 2. Проверка размера (например, максимум 5 МБ)
        long maxSize = 5 * 1024 * 1024; // 5 МБ
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Размер файла превышает допустимый лимит");
        }

        // 3. Генерация нового имени (UUID)
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf('.'));
        String newName = UUID.randomUUID().toString() + extension;

        // 4. Создание целевой директории
        Path uploadPath = Paths.get(uploadDir).normalize().toAbsolutePath().resolve(newName);
        Files.createDirectories(uploadPath.getParent());

        // 5. Копирование файла
        Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Файл успешно сохранён: {} (размер: {} байт)", newName, file.getSize());

        // 6. Возвращаем относительный путь или URL
        return "/uploads/" + newName;
    }

    private boolean isValidMimeType(String mimeType) {
        // Разрешите только нужные MIME-типы
        return mimeType != null && (
            mimeType.startsWith("image/") ||
            mimeType.startsWith("application/pdf") ||
            mimeType.equals("text/plain")
        );
    }

    public  ImportResult importFromCsv(MultipartFile file) {
        List<String> errors = new ArrayList<>(); // Список ошибок
        //List<StudentDto> students = new ArrayList<>(); // Результаты (созданные объекты)
        int successCount = 0;
        if (file == null || file.isEmpty()) {
            errors.add("Файл не может быть пустым");
            return new ImportResult(0, 0, 0, errors);
        }

        // 1. Проверка MIME-типа
        String contentType = file.getContentType();
        log.debug("MIME-тип файла: {}", contentType);
        if (!isValidMimeType(contentType)) {
            errors.add("Неподдерживаемый тип файла: " + contentType);
            return new ImportResult(0, 0, 0, errors);
        }

        // 2. Проверка размера (например, максимум 5 МБ)
        long maxSize = 5 * 1024 * 1024; // 5 МБ
        if (file.getSize() > maxSize) {
            errors.add("Размер файла превышает допустимый лимит (5 МБ)");
            return new ImportResult(0, 0, 0, errors);
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(reader,
                    CSVFormat.DEFAULT.builder()
                            .setHeader() // Первую строку считаем заголовком
                            .setSkipHeaderRecord(true) // Пропускаем заголовок при итерации
                            .setTrim(true) // Удаляем пробелы по краям
                            .build())){
            int total = 0;
            for (CSVRecord record : csvParser) {
                total++;
                 try {
                    // Создать новый объект StudentDto -> Валидация, record.get("столбец")
                    Long id = record.get("id") != null ? Long.parseLong(record.get("id")) : null;
                    String name = record.get("name");
                    String groupName = record.get("groupName"); // ← теперь есть!
                    String username = record.get("username");
                    String password = record.get("password");
                    

                    // Создаем DTO
                    StudentImportDto studentDto = new StudentImportDto(id, name, groupName, username, password);
                    studentService.create(studentDto);
                    successCount++;

                } catch (Exception e) {
                    // Добавить ошибку в список
                    errors.add("Ошибка при обработке строки " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }
            int failureCount = total - successCount;
            return new ImportResult(total, successCount, failureCount, errors);

        } catch (IOException e) {
            // Логирование ошибки
            errors.add("Ошибка при чтении файла: " + e.getMessage());
            return new ImportResult(0, 0, 0, errors);
        }
        
    }
    public String writeErrorLogFile(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            return null;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "import_errors_" + timestamp + ".txt";
        Path errorPath = Paths.get(uploadDir).normalize().toAbsolutePath().resolve(filename);

        try {
            Files.createDirectories(errorPath.getParent());
            Files.write(errorPath, errors, StandardCharsets.UTF_8);
            log.info("Файл ошибок создан: {}", errorPath);
            return "/uploads/" + filename; // относительный путь для возврата клиенту
        } catch (IOException e) {
            log.error("Не удалось записать файл ошибок", e);
            return null;
        }
    }
}
