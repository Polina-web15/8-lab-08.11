package com.example.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.example.demo.dto.ImportResult;
import com.example.demo.model.HeatmapData;
import com.example.demo.model.TimeEntry;
import com.example.demo.repository.TimeEntryRepository;
import com.example.demo.service.FileService;
import com.example.demo.service.StudentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    private final StudentService studentService;
    private final TimeEntryRepository timeEntryRepository;
    private final TemplateEngine templateEngine;

    @PostMapping(value = "/upload", consumes = "multipart/form-data") //загрузка файла + хранение
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

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)//работа с ошибками
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
    
    public static String getColor(int minutes) {
    if (minutes == 0) return "#f8f9fa";
    if (minutes < 30) return "#ffcccc";
    if (minutes < 60) return "#ff9999";
    if (minutes < 120) return "#ff6666";
    return "#ff0000";}

    // Внутри FileController
    private byte[] generatePdfFromHtml(String html) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IOException("Ошибка генерации PDF", e);
        }
    }

    @GetMapping("/report/heatmap/{studentId}")
    public ResponseEntity<byte[]> getHeatmapPdf(@PathVariable Long studentId) throws Exception {
        // Получаем записи за неделю
        List<TimeEntry> entries = timeEntryRepository.findByStudentIdAndStartAfter(
        studentId, LocalDateTime.now().minusDays(7));
    
        HeatmapData heatmap = new HeatmapData(studentId, entries);
        Context context = new Context();
        context.setVariable("heatmap", heatmap);
        context.setVariable("utils", this); // чтобы вызывать getColor()
        String html = templateEngine.process("heatmap", context);
        byte[] pdf = generatePdfFromHtml(html);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=heatmap_" + studentId + ".pdf")
            .body(pdf);
        }
}

