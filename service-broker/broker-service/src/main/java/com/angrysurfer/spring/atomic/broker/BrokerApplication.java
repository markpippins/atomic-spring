package com.angrysurfer.spring.atomic.broker;

import com.angrysurfer.spring.atomic.admin.logging.LoggingModuleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrokerApplication.class, args);
    }
}