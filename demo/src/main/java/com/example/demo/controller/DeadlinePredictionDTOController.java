package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DeadlinePredictionDTO;

import jakarta.validation.Valid;





@RestController
public class DeadlinePredictionDTOController {

    private List<DeadlinePredictionDTO> subjects = new ArrayList<>(Arrays.asList());
    //risk = "High" if (end > deadline)
    //hoursLeft = end - starts

    @GetMapping("/subjects")
    public List<DeadlinePredictionDTO> getSubjects() {
        return subjects;
    }
    
    
    @PostMapping("/subjects")
    public ResponseEntity<DeadlinePredictionDTO> addSubject(@RequestBody @Valid DeadlinePredictionDTO subject) {
        //subject.setId((long)subjects.size() + 1);
        subjects.add(subject);
        return ResponseEntity.status(HttpStatus.CREATED).body(subject);
    }   
}
