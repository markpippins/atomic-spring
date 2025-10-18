package com.angrysurfer.atomic.fs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.angrysurfer.atomic.fs.api.FsListResponse;
import com.angrysurfer.atomic.fs.api.FsRequest;

@Service("restFsClient")
public class RestFsClient {

    private final RestTemplate restTemplate;

    @Value("${restfs.api.url}")
    private String apiUrl;

    public RestFsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private <T> T post(FsRequest request, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FsRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<T> response = restTemplate.postForEntity(apiUrl, entity, responseType);
        return response.getBody();
    }

    // ===============================
    // Directory Operations
    // ===============================

    public FsListResponse listFiles(String alias, List<String> path) {
        FsRequest req = new FsRequest(alias, path, "ls");
        return post(req, FsListResponse.class);
    }

    public Map<String, Object> changeDirectory(String alias, List<String> path) {
        FsRequest req = new FsRequest(alias, path, "cd");
        return post(req, Map.class);
    }

    public Map<String, Object> createDirectory(String alias, List<String> path) {
        FsRequest req = new FsRequest(alias, path, "mkdir");
        return post(req, Map.class);
    }

    public Map<String, Object> removeDirectory(String alias, List<String> path) {
        FsRequest req = new FsRequest(alias, path, "rmdir");
        return post(req, Map.class);
    }

    // ===============================
    // File Operations
    // ===============================

    public Map<String, Object> createFile(String alias, List<String> path, String filename) {
        FsRequest req = new FsRequest(alias, path, "newfile");
        req.setFilename(filename);
        return post(req, Map.class);
    }

    public Map<String, Object> deleteFile(String alias, List<String> path, String filename) {
        FsRequest req = new FsRequest(alias, path, "deletefile");
        req.setFilename(filename);
        return post(req, Map.class);
    }

    public Map<String, Object> rename(String alias, List<String> path, String newName) {
        FsRequest req = new FsRequest(alias, path, "rename");
        req.setNewName(newName);
        return post(req, Map.class);
    }

    public Map<String, Object> copy(String fromAlias, List<String> fromPath, String toAlias, List<String> toPath) {
        FsRequest req = new FsRequest(fromAlias, fromPath, "copy");
        req.setToAlias(toAlias);
        req.setToPath(toPath);
        return post(req, Map.class);
    }

    public Map<String, Object> move(String fromAlias, List<String> fromPath, String toAlias, List<String> toPath) {
        FsRequest req = new FsRequest(fromAlias, fromPath, "move");
        req.setToAlias(toAlias);
        req.setToPath(toPath);
        return post(req, Map.class);
    }
}
