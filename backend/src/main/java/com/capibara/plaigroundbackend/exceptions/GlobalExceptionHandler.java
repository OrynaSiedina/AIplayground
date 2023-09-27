package com.capibara.plaigroundbackend.exceptions;

import com.capibara.plaigroundbackend.models.dtos.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorisedException.class)
    public ResponseEntity<ErrorDTO> handleUnauthorisedException(UnauthorisedException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(createErrorDTO(ex));
    }

    @ExceptionHandler(DataRequiredException.class)
    public ResponseEntity<ErrorDTO> handleDataRequiredException(DataRequiredException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(createErrorDTO(ex));
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorDTO> handleInvalidDataException(InvalidDataException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(createErrorDTO(ex));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(createErrorDTO(ex));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(createErrorDTO(ex));
    }

    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ErrorDTO> handleAccessForbidden(AccessForbiddenException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(createErrorDTO(ex));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorDTO> handleServerException(ServerException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(createErrorDTO(ex));
    }

    @ExceptionHandler(AIPlaygroundException.class)
    public ResponseEntity<ErrorDTO> handleAIPlaygroundException(AIPlaygroundException ex) {

        ErrorDTO errorDTO = createErrorDTO(ex);

        return ResponseEntity.status(errorDTO.getStatusCode()).body(errorDTO);
    }

    private ErrorDTO createErrorDTO(AIPlaygroundException ex) {
        return new ErrorDTO(
                ex.getHttpStatusCode(),
                ex.getMessage()
        );
    }
}
