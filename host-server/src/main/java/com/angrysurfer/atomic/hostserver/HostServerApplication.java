package com.angrysurfer.atomic.hostserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.angrysurfer.atomic.hostserver.repository")
public class HostServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HostServerApplication.class, args);
    }
}