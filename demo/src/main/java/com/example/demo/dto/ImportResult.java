package com.example.demo.dto;

import java.util.List;

public record ImportResult(
    int total,
    int failure,
    int successCount,
    List<String> errors
) {}
