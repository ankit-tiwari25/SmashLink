package com.project.smashlink.user.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value=CustomUserException.class)
    public ResponseEntity<Map<String,String>> handleException(Exception e, HttpServletRequest request) {
        Map<String,String> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
