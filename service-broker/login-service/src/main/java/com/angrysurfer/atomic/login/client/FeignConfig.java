package com.angrysurfer.atomic.login.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.angrysurfer.atomic.login.client")
public class FeignConfig {
}
