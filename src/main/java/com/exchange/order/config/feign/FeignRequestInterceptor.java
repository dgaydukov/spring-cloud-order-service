package com.exchange.order.config.feign;

import com.exchange.order.config.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest webRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
            webRequest.getHeaderNames()
                    .asIterator()
                    .forEachRemaining(h -> {
                        if (Constants.HEADER_ACCEPT_LANGUAGE.equalsIgnoreCase(h)) {
                            template.header(h, webRequest.getHeader(h));
                        }
                    });
        }
    }
}