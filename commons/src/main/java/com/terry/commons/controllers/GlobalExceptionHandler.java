package com.terry.commons.controllers;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.terry.commons.exceptions.EntidadRelacionadaException;

import feign.FeignException;
import feign.RetryableException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());
	// Validar restricciones de anotaciones como @NotNull, @Min, @Max, etc...
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException e) {
		LOGGER.log(Level.WARNING, "Violación de restricción: " +
				(e.getCause() != null ? e.getCause() : e.getMessage()));
		return ResponseEntity.badRequest().body(Map.of(
				"code", HttpStatus.BAD_REQUEST.value(),
				"response", "Violación de restricción: " + e.getMessage()
		));
	}
	
	// Validar fallidas en los dto
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		LOGGER.log(Level.WARNING, "Error de validación de argumentos: " + e.getMessage());
		String mensaje = e.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage())
				.findFirst()
				.orElse("Error de validación en los datos enviados");
		return ResponseEntity.badRequest().body(Map.of(
				"code", HttpStatus.BAD_REQUEST.value(),
				"response", mensaje
		)); 
	}
	
	// No se encontró el recurso solicitado
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Map<String, Object>> handleNoSuchElementException(NoSuchElementException e) {
		LOGGER.log(Level.WARNING, e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
				"code", HttpStatus.NOT_FOUND.value(),
				"response", "No se encontró información asociada con el identificador ingresado"
		));
	}
	
	
	// Excepciones Feign genéricas (incluye 404, 401, 403, 503, etc.)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleGenericFeignException(FeignException e) {
        LOGGER.log(Level.SEVERE, "Error en la comunicación Feign: " + e.getMessage());

        int status = e.status() > 0 ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = switch (status) {
            case 400 -> "Solicitud incorrecta al servicio remoto.";
            case 401 -> "No autorizado para acceder al servicio remoto.";
            case 403 -> "Acceso prohibido al servicio remoto.";
            case 404 -> "Recurso no encontrado en el servicio remoto.";
            case 503 -> "Servicio remoto no disponible.";
            default -> "Error al comunicarse con el servicio remoto.";
        };

        return ResponseEntity.status(status).body(Map.of(
                "code", status,
                "response", message
        ));
    }

    // Cuando el servicio remoto no responde o está caído
    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<Map<String, Object>> handleRetryable(RetryableException e) {
    	LOGGER.log(Level.SEVERE, "Servicio remoto no disponible o no responde: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "code", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "response", "El servicio remoto no está disponible o no responde en este momento."
        ));
    }
   
    
    @ExceptionHandler(EntidadRelacionadaException.class)
    public ResponseEntity<Map<String, Object>> entidadRelacionadaException(EntidadRelacionadaException e) {
    	LOGGER.log(Level.WARNING, "Error al eliminar el recurso: " + e.getMessage());
	    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "code", HttpStatus.CONFLICT.value(),
                "response", e.getMessage()
        ));
    }
	
	// Cualquier otro error que no esté registrado
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
		LOGGER.log(Level.SEVERE, "Error interno del servidor: " +
			(e.getCause() != null ? e.getCause() : e.getMessage()));
		return ResponseEntity.badRequest().body(Map.of(
			"code", HttpStatus.INTERNAL_SERVER_ERROR.value(),
			"response", "Error interno del servidor: " + e.getMessage()
		));
	}

}
