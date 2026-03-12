package com.angrysurfer.spring.atomic.search;

import lombok.Data;
import java.util.List;

@Data
public class SearchResult {
    private List<SearchResultItem> items;
    private Object rawResponse;
}