package com.exchange.order.config;

import com.exchange.order.domain.AppError;
import com.exchange.order.exception.AppException;
import com.exchange.order.service.MessageTranslationService;
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
    private final MessageTranslationService messageTranslationService;

    private String getTraceId() {
        if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
            return tracer.currentSpan().context().traceId();
        }
        return null;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppError handleException(HttpServletRequest request, Exception ex) {
        log.error("Catch Exception: url={}", request.getRequestURI(), ex);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        String userMsg = messageTranslationService.getMessage(errorCode.getErrorCode());
        return new AppError(errorCode.getCode(), errorCode.getErrorCode(), userMsg, getTraceId());
    }


    @ExceptionHandler({AppException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleAppException(HttpServletRequest req, AppException ex) {
        log.error("Catch AppException: url={}", req.getRequestURI(), ex);
        return new AppError(ex.getCode(), ex.getErrorCode(), ex.getMsg(), getTraceId());
    }
}