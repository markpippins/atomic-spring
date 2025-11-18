package com.angrysurfer.atomic.search;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;

@Service("googleSearchService")
public class GoogleSearchService {

    private static final Logger log = LoggerFactory.getLogger(GoogleSearchService.class);

    private final RestTemplate restTemplate;
    
    // Cache for search results - maps query to search result
    private final Map<String, SearchResult> searchCache = new ConcurrentHashMap<>();

    // @Value("${google.search.api.key:#{null}}")
    private String googleApiKey = "AIzaSyAfVHkNv8-YVyz1eSitseZLTHcXW4NTyI4";

    // @Value("${google.search.engine.id:#{null}}")
    private String searchEngineId = "e44fd2743cc9e49c8";

    @Autowired
    public GoogleSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        log.info("GoogleSearchService initialized with cache");
    }

    @BrokerOperation("simpleSearch")
    public SearchResult simpleSearch(@BrokerParam("token") String token, @BrokerParam("query") String query) {
        log.info("Query Received: {}", query);

        // First, check if we have a cached result for this query
        SearchResult cachedResult = searchCache.get(query);
        if (cachedResult != null) {
            log.info("Returning cached result for query: {}", query);
            return cachedResult;
        }

        // Validate configuration
        if (googleApiKey == null || googleApiKey.isEmpty()) {
            throw new IllegalStateException("Google API Key is required. Please provide it in application.properties.");
        }

        if (searchEngineId == null || searchEngineId.isEmpty()) {
            throw new IllegalStateException("Search Engine ID is required. Please provide it in application.properties.");
        }

        // Properly URL encode the query
        String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        String url = String.format("https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&q=%s", 
                                  googleApiKey, searchEngineId, encodedQuery);

        try {
            log.debug("Making request to: {}", url);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> data = response.getBody();
                
                // Extract items from the response
                List<Map<String, Object>> rawItems = (List<Map<String, Object>>) data.get("items");
                List<SearchResultItem> items = new ArrayList<>();
                
                if (rawItems != null) {
                    for (Map<String, Object> rawItem : rawItems) {
                        SearchResultItem item = new SearchResultItem();
                        item.setKind((String) rawItem.get("kind"));
                        item.setTitle((String) rawItem.get("title"));
                        item.setHtmlTitle((String) rawItem.get("htmlTitle"));
                        item.setLink((String) rawItem.get("link"));
                        item.setDisplayLink((String) rawItem.get("displayLink"));
                        item.setSnippet((String) rawItem.get("snippet"));
                        item.setHtmlSnippet((String) rawItem.get("htmlSnippet"));
                        item.setFormattedUrl((String) rawItem.get("formattedUrl"));
                        item.setHtmlFormattedUrl((String) rawItem.get("htmlFormattedUrl"));
                        Map<String, Object> pagemap = (Map<String, Object>) rawItem.get("pagemap");
                        item.setPagemap(pagemap);
                        
                        // Extract metatags and other specific data from pagemap if available
                        if (pagemap != null) {
                            item.setMetatags((List<Map<String, String>>) pagemap.get("metatags"));
                            item.setCseThumbnail((List<Map<String, String>>) pagemap.get("cse_thumbnail"));
                            item.setCseImage((List<Map<String, String>>) pagemap.get("cse_image"));
                        }
                        
                        items.add(item);
                    }
                }

                SearchResult result = new SearchResult();
                result.setItems(items);
                result.setRawResponse(data);
                
                // Cache the result before returning
                searchCache.put(query, result);
                log.info("Cached result for query: {}", query);
                log.info("Result: {}", result);
                
                return result;
            } else {
                log.error("Google search API returned error: {}", response.getStatusCode());
                throw new RuntimeException("Google search API returned error: " + response.getStatusCode());
            }
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("Network error performing Google search: {}", e.getMessage());
            throw new RuntimeException("Network connection failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error performing Google search: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch search results: " + e.getMessage());
        }
    }
}