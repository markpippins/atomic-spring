package com.angrysurfer.atomic.fs.api;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FsListResponse {
    
    private List<String> path;
    private List<FsItem> items;
}
