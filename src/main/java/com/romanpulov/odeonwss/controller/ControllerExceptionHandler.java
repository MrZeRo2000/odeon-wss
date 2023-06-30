package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ErrorResponseDTO;
import com.romanpulov.odeonwss.exception.CommonEntityAlreadyExistsException;
import com.romanpulov.odeonwss.exception.DataNotFoundException;
import com.romanpulov.odeonwss.exception.EmptyParameterException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(DataNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongParameterValueException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadPatchRequest(WrongParameterValueException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadPatchRequest(EmptyParameterException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommonEntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleAlreadyExists(CommonEntityAlreadyExistsException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex,HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
