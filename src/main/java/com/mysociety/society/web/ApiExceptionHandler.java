package com.mysociety.society.web;

import java.net.URI; import java.util.NoSuchElementException; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.resource.NoResourceFoundException;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class) ProblemDetail notFound(NoSuchElementException ex) { return problem(HttpStatus.NOT_FOUND, ex.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class) ProblemDetail badRequest(IllegalArgumentException ex) { return problem(HttpStatus.BAD_REQUEST, ex.getMessage()); }
    @ExceptionHandler(NoResourceFoundException.class) ProblemDetail missing(NoResourceFoundException ex) { return problem(HttpStatus.NOT_FOUND, "Resource not found"); }
    private ProblemDetail problem(HttpStatus status, String detail) { ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail); p.setType(URI.create("https://mysociety.dev/problems/" + status.value())); return p; }
}
