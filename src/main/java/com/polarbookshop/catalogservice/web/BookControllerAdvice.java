package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BookControllerAdvice {

  @ExceptionHandler(Exception.class)
  public ProblemDetail exceptionHandler(Exception exception) {
    return getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception);
  }

  @ExceptionHandler(BookNotFoundException.class)
  public ProblemDetail bookNotFoundExceptionHandler(BookNotFoundException exception) {
    return getProblemDetail(HttpStatus.NOT_FOUND, exception);
  }

  @ExceptionHandler(BookAlreadyExistsException.class)
  public ProblemDetail bookAlreadyExistsExceptionHandler(BookAlreadyExistsException exception) {
    return getProblemDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail methodArgumentNotValidExceptionHandler(
      MethodArgumentNotValidException exception) {
    ProblemDetail problemDetail = getProblemDetail(HttpStatus.BAD_REQUEST, exception);

    List<String> errors = new ArrayList<>();
    exception
        .getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.add(fieldName + ": " + errorMessage);
            });
    problemDetail.setProperty("errors", errors);

    return problemDetail;
  }

  private static ProblemDetail getProblemDetail(HttpStatus status, Exception exception) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());

    problemDetail.setProperty("timestamp", LocalDateTime.now());
    problemDetail.setProperty("exception", exception.getClass().getSimpleName());

    return problemDetail;
  }
}
