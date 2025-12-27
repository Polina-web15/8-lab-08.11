package com.example.demo.service;

//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.StudentDto;
import com.example.demo.dto.StudentImportDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.specification.StudentSpecifications;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly=true)
public class StudentService{

    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
   // private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder){
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //private List<Student> students = new ArrayList<>(Arrays.asList());
    //private AtomicLong IdGenerator = new AtomicLong();

    @PostConstruct
    public void unit(){}
    public List<Student> getAll(){
        return studentRepository.findAll();
    }
    @Cacheable(value="students", key="name")
    public List<Student> getAllByName(String name){
        return studentRepository.findAllByName(name);
    }
    @CacheEvict(value="students", allEntries=true)//нужно
    @Transactional
    public Student create(Student student){
        log.info("Студент успешно создан и сохранён в БД: id={}, username={}", 
        student.getUsername());
        return studentRepository.save(student);
    }
    @Cacheable(value="students", key="#id")
    public Student getById(Long id) {//true
        log.debug("Получение студента по id={} (с кэшированием)", id);
        return studentRepository.findById(id).orElseThrow(() -> {
            log.warn("Студент не найден по id: {}", id);
            return new ResourceNotFoundException("Student with id " + id + " not found");
        });
            
    }
     
    public Student update(Long id, Student student) {//true
        log.info("Обновление студента с id={}", id);
        return studentRepository.findById(id).map(existingStudent -> {
            existingStudent.setName(student.getName());
            existingStudent.setGroupName(student.getGroupName());
            existingStudent.setRecentEntries(student.getRecentEntries());
            log.info("Студент успешно обновлён: id={}, username={}");
            return studentRepository.save(existingStudent);
        }).orElseGet(() -> {
            log.warn("Попытка обновить несуществующего студента с id={}", id);
            return null;
    });
    }
    @Caching(evict={
         @CacheEvict(value="students", allEntries=true),       
    })
    public boolean deleteById(Long id) {//true
        log.info("Удаление студента с id={}", id);
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        else {
            log.warn("Попытка удалить несуществующего студента с id={}", id);
            return false;
        }
    }
    public Page<Student> getByFilter(String name, String groupName,  Pageable pageable){//переделать
        return  studentRepository.findAll(StudentSpecifications.filter(name, groupName), pageable);
    }
//
    public List<StudentDto> getStudents() {
        return studentRepository.findAll().stream().map(StudentMapper::studentToStudentDto).toList();
    }

    public StudentDto getStudent(Long id) {
    Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student with id" + id + " not found"));
        return StudentMapper.studentToStudentDto(student);
    }
  
    public StudentDto  getStudentDto(String username) {  
        return StudentMapper.studentToStudentDto(getStudent(username));
    }
    public Student  getStudent(String username) {  
        log.debug("Поиск студента по username: {}", username);
        Student student = studentRepository.findByUsername(username).orElseThrow(() ->
        {
            log.warn("Студент не найден по username: {}", username);
            return new UsernameNotFoundException("Student with username " + username + " not found");
        });
        log.debug("Студент найден: id={}, username={}", student.getId(), student.getUsername());
        return student;
    }
   
    @Transactional
    public Student create(StudentImportDto dto) {
        Student student = new Student();
        student.setId(dto.id());
        student.setName(dto.name());
        student.setUsername(dto.username()+"_"+dto.id());
        student.setPassword(passwordEncoder.encode(dto.password()));
        student.setGroupName("Default Group");
        log.info("Студент успешно создан: id={}, username={}", student.getId(), student.getUsername());
        return studentRepository.save(student);
        }
}
