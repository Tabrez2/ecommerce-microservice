package com.tab.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{

    @ExceptionHandler
    public ResponseEntity<String> handler(ProductNotFoundException exp) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exp.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handler(ProductPurchaseException exp) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exp.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handler(MethodArgumentNotValidException exp) {

        var errors = new HashMap<String,String>();

        exp.getBindingResult().getAllErrors().forEach((error) -> {
            var errorMessage = error.getDefaultMessage();
            var fieldName = ((FieldError) error).getField();
            errors.put(fieldName, errorMessage);
        });

        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(errors));
    }

}
