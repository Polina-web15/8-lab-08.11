package com.example.demo.controller;

//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;


import com.example.demo.model.Student;
import com.example.demo.service.StudentService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;



@RequestMapping("/api")
@RestController
public class StudentController {
    private final StudentService studentService;

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
   // private List<Student> students = new ArrayList<>(Arrays.asList());
    //recentEntries = times для однго студента,
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }
    @GetMapping("/students")
    public List<Student> getStudents() {
        logger.debug("Получен запрос на получение всех студентов");
        return studentService.getAll();
    }
    
    @PutMapping("students/{id}")
    public ResponseEntity<Student> edit(@PathVariable Long id, @RequestBody Student student) {
        logger.info("Получен запрос на обновление студента с id={}", id);
        try{
            Student updated = studentService.update(id, student);
            if (updated != null) {
                logger.info("Студент с id={} успешно обновлён", id);
                return ResponseEntity.ok(updated);
            } else {
                logger.warn("Попытка обновить несуществующего студента с id={}", id);
                return ResponseEntity.notFound().build();
            }
        }
        catch (Exception e) {
            logger.error("Ошибка при обновлении студента с id={}", id, e);
            throw e;
        }
    }
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudents(@PathVariable Long id) {//true
        logger.debug("Получен запрос на получение студента с id={}", id);
        Student p = studentService.getById(id);
        if(p != null){
            logger.debug("Найден студент: id={}, username={}", p.getId(), p.getUsername());
            return ResponseEntity.ok(p);
        }
        else {
            logger.warn("Студент с id={} не найден", id);
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/students/{id}")
     public ResponseEntity<Void> delete(@PathVariable Long id){
        logger.info("Получен запрос на удаление студента с id={}", id);
        if(studentService.deleteById(id)){
            logger.info("Студент с id={} успешно удалён", id);
            return ResponseEntity.noContent().build();
        }
        else {
            logger.warn("Попытка удалить несуществующего студента с id={}", id);
            return ResponseEntity.notFound().build(); // ← лучше 404, а не 200
        }
    }

    @PostMapping("/students")
    public ResponseEntity<Student> addStudent(@RequestBody @Valid Student student) {
        logger.info("Получен запрос на создание студента: username={}", 
            student.getUsername() != null ? student.getUsername() : "N/A");
        try {
            Student newStudent = studentService.create(student);
            logger.info("Студент успешно создан: id={}, username={}", 
                newStudent.getId(), newStudent.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(newStudent);
        } catch (Exception e) {
            logger.error("Ошибка при создании студента: username={}", 
                student.getUsername() != null ? student.getUsername() : "N/A", e);
            throw e;
        }
    }
    @GetMapping("/filter")
    public ResponseEntity<Object> getByFilter(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String groupName,
    @PageableDefault(page=0, size=10, sort="name")
    Pageable pageable) {
        logger.debug("Найдено записей по фильтру: name={}, groupName={}", name, groupName);
        return ResponseEntity.ok(studentService.getByFilter(name, groupName, pageable));
    }
}
