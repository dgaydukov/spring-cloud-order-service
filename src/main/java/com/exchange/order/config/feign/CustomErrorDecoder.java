package com.exchange.order.config.feign;

import com.exchange.order.exception.ApiGenericException;
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
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        HttpStatus status = HttpStatus.valueOf(response.status());

        String body = null;
        try{
            body = new String(response.body().asInputStream().readAllBytes());
        } catch (IOException ex){
            log.error("Failed to convert body InputStream into string: requestUrl={}", requestUrl);
        }

//        if (status.is5xxServerError()) {
//            return new ApiServerException(requestUrl, body);
//        } else if (status.is4xxClientError()) {
//            return new ApiClientException(requestUrl, body);
//        } else {
//            return new ApiGenericException(requestUrl, body);
//        }
        return new ApiGenericException(requestUrl, body);
    }
}
