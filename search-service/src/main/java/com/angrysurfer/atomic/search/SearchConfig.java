package com.angrysurfer.atomic.search;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SearchConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}