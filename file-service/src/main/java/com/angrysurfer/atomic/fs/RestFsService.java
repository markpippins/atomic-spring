package com.angrysurfer.atomic.fs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.fs.api.FsListResponse;

@Service
@Qualifier("restFsService")
public class RestFsService {

    private final RestFsClient restFsClient;
    private final ReactiveRestFsClient reactiveRestFsClient;

    public RestFsService(@Qualifier("restFsClient") RestFsClient restFsClient,
            @Qualifier("reactiveRestFsClient") ReactiveRestFsClient reactiveRestFsClient) {
        this.restFsClient = restFsClient;
        this.reactiveRestFsClient = reactiveRestFsClient;
    }

    @BrokerOperation("listFiles")
    public FsListResponse listFiles(@BrokerParam("alias") String alias, @BrokerParam("path") List<String> path) {
        return restFsClient.listFiles(alias, path);
    }

    @BrokerOperation("changeDirectory")
    public Map<String, Object> changeDirectory(@BrokerParam("alias") String alias, @BrokerParam("path") List<String> path) {
        return restFsClient.changeDirectory(alias, path);
    }

    @BrokerOperation("createDirectory")
    public Map<String, Object> createDirectory(@BrokerParam("alias") String alias, @BrokerParam("path") List<String> path) {
        return restFsClient.createDirectory(alias, path);
    }

    @BrokerOperation("removeDirectory")
    public Map<String, Object> removeDirectory(@BrokerParam("alias") String alias, @BrokerParam("path") List<String> path) {
        return restFsClient.removeDirectory(alias, path);
    }

    @BrokerOperation("createFile")
    public Map<String, Object> createFile(@BrokerParam("alias") String alias, @BrokerParam("path") List<String> path,
            @BrokerParam("filename") String filename) {
        return restFsClient.createFile(alias, path, filename);
    }

    @BrokerOperation("deleteFile")
    public Map<String, Object> deleteFile(@BrokerParam("alias") String alias, @BrokerParam("path") List<String> path,
            @BrokerParam("filename") String filename) {
        return restFsClient.deleteFile(alias, path, filename);
    }

    @BrokerOperation("rename")
    public Map<String, Object> rename(@BrokerParam("alias") String alias, @BrokerParam("path") List<String> path,
            @BrokerParam("newName") String newName) {
        return restFsClient.rename(alias, path, newName);
    }

    @BrokerOperation("copy")
    public Map<String, Object> copy(@BrokerParam("fromAlias") String fromAlias,
            @BrokerParam("fromPath") List<String> fromPath, @BrokerParam("toAlias") String toAlias,
            @BrokerParam("toPath") List<String> toPath) {
        return restFsClient.copy(fromAlias, fromPath, toAlias, toPath);
    }

}