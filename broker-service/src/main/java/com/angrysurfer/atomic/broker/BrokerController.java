package com.angrysurfer.atomic.broker;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;

// @CrossOrigin("*")
@RestController
@RequestMapping("/api/broker")
public class BrokerController {

    private static final Logger log = LoggerFactory.getLogger(BrokerController.class);

    private final Broker broker;

    public BrokerController(Broker broker) {
        this.broker = broker;
        log.info("BrokerController initialized");
    }

    @PostMapping(value = "/testBroker")
    public ResponseEntity<?> testBroker() {
        // log.debug("Received request: {}", request);

        ServiceRequest request = new ServiceRequest("testBroker", "test",
                Collections.emptyMap(), "test-request");

        ServiceResponse<?> response = broker.submit(request);

        if (response.isOk()) {
            return ResponseEntity.ok(response);
        } else {
            // decide on HTTP code: validation errors = 400, not_found = 404, etc.
            // simplest case: always return 400 for errors
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "/submitRequest", consumes = {"application/json"})
    public ResponseEntity<?> submitRequest(@RequestBody ServiceRequest request) {
        log.debug("Received request: {}", request);

        ServiceResponse<?> response = broker.submit(request);

        if (response.isOk()) {
            return ResponseEntity.ok(response);
        } else {
            // decide on HTTP code: validation errors = 400, not_found = 404, etc.
            // simplest case: always return 400 for errors
            log.debug("returning: {}", response);
            return ResponseEntity.badRequest().body(response);
        }
    }

}
