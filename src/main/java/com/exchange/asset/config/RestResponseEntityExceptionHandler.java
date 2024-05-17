package com.exchange.asset.config;

import com.exchange.asset.domain.AppError;
import com.exchange.asset.exception.AppException;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Tracer tracer;


    @ExceptionHandler({AppException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleAppException(HttpServletRequest req, AppException ex) {
        String traceId = null;
        if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
            traceId = tracer.currentSpan().context().traceId();
        }
        log.error("catch AppException: url={}", req.getRequestURI(), ex);
        return new AppError(ex.getCode(), ex.getErrorCode(), ex.getMsg(), traceId);
    }
}