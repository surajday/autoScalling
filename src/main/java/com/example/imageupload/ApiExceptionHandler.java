package com.example.imageupload;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(StoreCapacityExceededException.class)
    public ResponseEntity<Map<String, Object>> capacityExceeded(StoreCapacityExceededException exception) {
        return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.INSUFFICIENT_STORAGE.value(),
                "error", exception.getMessage(),
                "capacityBytes", exception.getCapacityBytes(),
                "usedBytes", exception.getUsedBytes(),
                "requestedBytes", exception.getRequestedBytes()));
    }
}
