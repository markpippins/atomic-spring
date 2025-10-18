package com.angrysurfer.atomic.vaadin.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BrokerClient {

    @Value("${broker.url:http://localhost:8080/api/broker}")
    private String brokerUrl;

    private final RestTemplate restTemplate;

    private final String clientId;

    public BrokerClient() {
        this.restTemplate = new RestTemplate();
        this.clientId = "vaadin-client";
    }

    public <T> ServiceResponse<T> submitRequest(
            String service,
            String operation,
            Map<String, Object> params,
            ParameterizedTypeReference<ServiceResponse<T>> responseType
    ) {
        log.info(
                "Submitting request to service: {}, operation: {}, params: {}",
                service,
                operation,
                params
        );
        try {
            ServiceRequest request = new ServiceRequest(
                    service,
                    operation,
                    params,
                    clientId + "-" + System.currentTimeMillis()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ServiceRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ServiceResponse<T>> response = restTemplate.exchange(
                    brokerUrl + "/submitRequest",
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Broker request failed", e);
            return createErrorResponse(e.getMessage());
        }
    }

    public <T> ServiceResponse<T> submitRequestWithFile(
            String service,
            String operation,
            Map<String, Object> params,
            byte[] fileContent,
            String fileName,
            String contentType,
            ParameterizedTypeReference<ServiceResponse<T>> responseType
    ) {
        log.info(
                "Submitting request with file to service: {}, operation: {}, fileName: {}",
                service,
                operation,
                fileName
        );
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("service", service);
            form.add("operation", operation);
            form.add("params", params);
            form.add("requestId", clientId + "-" + System.currentTimeMillis());

            ByteArrayResource fileResource = new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            form.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(
                    form,
                    headers
            );

            ResponseEntity<ServiceResponse<T>> response = restTemplate.exchange(
                    brokerUrl + "/submitRequestWithFile",
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Broker file request failed", e);
            return createErrorResponse(e.getMessage());
        }
    }

    private <T> ServiceResponse<T> createErrorResponse(String message) {
        ServiceResponse<T> errorResponse = new ServiceResponse<>();
        errorResponse.setOk(false);
        errorResponse.setErrors(java.util.List.of(Map.of("message", message)));
        errorResponse.setTs(java.time.Instant.now());
        return errorResponse;
    }
}
