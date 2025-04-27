package com.banka1.banking.config;

import com.banka1.banking.dto.CreateEventDeliveryDTO;
import com.banka1.banking.models.Event;
import com.banka1.banking.models.EventDelivery;
import com.banka1.banking.models.helper.DeliveryStatus;
import com.banka1.banking.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Hidden
@ControllerAdvice
@RequiredArgsConstructor
public class InterbankDeliveryInterceptor implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(InterbankDeliveryInterceptor.class);
    private final EventService eventService;
    private final ObjectMapper mapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {


        if (!request.getURI().getPath().contains("/interbank")) return body;

        try {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();


            Event event = (Event) servletRequest.getAttribute("event");


            if (event != null) {
                long durationMs = System.currentTimeMillis() - (long) servletRequest.getAttribute("startTime");

                CreateEventDeliveryDTO dto = new CreateEventDeliveryDTO();
                dto.setEvent(event);
                dto.setStatus(DeliveryStatus.SUCCESS);
                dto.setHttpStatus(response.getHeaders().getFirst("status") != null
                        ? Integer.parseInt(response.getHeaders().getFirst("status"))
                        : 200);
                dto.setResponseBody(body != null ? mapper.writeValueAsString(body) : "");
                dto.setDurationMs(durationMs);

                EventDelivery delivery = eventService.createEventDelivery(dto);
                log.info("Successfully created event delivery: {}", delivery.getId());
                log.info("Body: {}", body);
            }

        } catch (Exception e) {
            log.error("Exception when creating event delivery", e);
        }

        return body;
    }
}
