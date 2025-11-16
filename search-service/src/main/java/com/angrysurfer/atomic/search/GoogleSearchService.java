package com.angrysurfer.atomic.search;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;


@Service("googleSearchService")
public class GoogleSearchService {

    private static final Logger log = LoggerFactory.getLogger(GoogleSearchService.class);

    public GoogleSearchService() {
        log.info("GoogleSearchService initialized");
    }

    @BrokerOperation("simpleSearch")
    public List<String> simpleSearch(@BrokerParam("token") String token, @BrokerParam("query") String query) {

        log.info("Query Received: {}", query);
        
        return List.of();
    }
}