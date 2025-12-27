package com.example.demo.dto;

import java.io.Serializable;

public record StudentImportDto(
Long id, 
String name, 
String username,
String groupName,
String password
) implements Serializable { }
