package com.romanpulov.odeonwss.controller;

import com.romanpulov.odeonwss.dto.ErrorResponseDTO;
import com.romanpulov.odeonwss.exception.CommonEntityNotFoundException;
import com.romanpulov.odeonwss.exception.WrongParameterValueException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(CommonEntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ChangeSetPersister.NotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongParameterValueException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadPatchRequest(WrongParameterValueException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
