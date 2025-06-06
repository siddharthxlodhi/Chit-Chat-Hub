package com.sid.chitchathub.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ExceptionResponse> handleChatException(ChatException chatException) {
        return ResponseEntity.badRequest().body(
                ExceptionResponse.builder().error(chatException.getMessage()).build()
        );
    }


}
