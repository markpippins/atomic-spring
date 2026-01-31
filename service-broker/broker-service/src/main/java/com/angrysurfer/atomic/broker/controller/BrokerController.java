package com.angrysurfer.atomic.broker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.broker.Broker;

@RestController
@RequestMapping("/api/broker")
public class BrokerController {

    @Autowired
    private Broker broker;

    @PostMapping("/submitRequest")
    public ResponseEntity<ServiceResponse<?>> submitRequest(@RequestBody ServiceRequest request) {
        try {
            ServiceResponse<?> response = broker.submit(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                ServiceResponse.error("broker", "submitRequest", 
                    java.util.List.of(java.util.Map.of("code", "broker_error", "message", e.getMessage())), 
                    request.getRequestId())
            );
        }
    }
}