package com.angrysurfer.atomic.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.angrysurfer")
@ComponentScan(basePackages = {"com.angrysurfer.atomic.broker", "com.angrysurfer.atomic.user", "com.angrysurfer.atomic.fs"})
public class BrokerGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrokerGatewayApplication.class, args);
	}

}
