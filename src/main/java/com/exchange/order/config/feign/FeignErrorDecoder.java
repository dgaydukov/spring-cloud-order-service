package com.exchange.order.config.feign;

import com.exchange.order.config.ErrorCode;
import com.exchange.order.domain.AppError;
import com.exchange.order.exception.AppException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String method, Response response) {
        String requestUrl = response.request().url();
        HttpStatus status = HttpStatus.valueOf(response.status());

        String body = null;
        try{
            body = new String(response.body().asInputStream().readAllBytes());
        } catch (IOException ex){
            log.error("Failed to convert body InputStream into string: requestUrl={}", requestUrl);
        }
        String requestBody = null;
        if (response.request().body() != null){
            requestBody = new String(response.request().body());
        }
        log.warn("Catch feign error: method={}, requestUrl={}, requestBody={}, body={}",
                method, requestUrl, requestBody, body);
        if (status.is5xxServerError()){
            FeignException ex = feign.FeignException.errorStatus(method, response);
            return new RetryableException(response.status(), ex.getMessage(),
                    response.request().httpMethod(), ex, 0L, response.request());
        }
        if (status.is4xxClientError()){
            try{
                AppError appError = objectMapper.readValue(body, AppError.class);
                return new AppException(appError.getCode(), appError.getErrorCode(), appError.getMsg());
            } catch (JsonProcessingException ex){
                log.error("Failed to convert body String into object: requestUrl={}, body={}", requestUrl, body);
            }
        }
        return new AppException(ErrorCode.FAILED_TO_PROCESS_REQUEST, null);
    }
}