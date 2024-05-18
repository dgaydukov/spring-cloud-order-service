package com.exchange.order.config.feign;

import com.exchange.order.config.ErrorCode;
import com.exchange.order.domain.AppError;
import com.exchange.order.exception.AppException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {
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
        log.warn("Catch feign error: method={}, requestUrl={}, body={}", method, requestUrl, body);
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