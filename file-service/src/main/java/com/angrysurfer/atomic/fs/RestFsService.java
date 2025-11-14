package com.angrysurfer.atomic.fs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.fs.api.FsListResponse;
import com.angrysurfer.atomic.login.LoginService;

@Service
@Qualifier("restFsService")
public class RestFsService {

    private final RestFsClient restFsClient;
    private final ReactiveRestFsClient reactiveRestFsClient;
    private final LoginService loginService;

    private List<String> getUserPath(String token, List<String> path) {
        var userPath = new ArrayList<String>();
        userPath.add("users");
        
        // Get the user alias from the login service using the token
        if (Objects.nonNull(token)) {
            var userResponse = loginService.getUserRegistrationForToken(token);
            if (userResponse.isOk() && userResponse.getData() != null) {
                String userAlias = userResponse.getData().getAlias();
                userPath.add(userAlias);
            } else {
                // In case of error, we might want to throw an exception or handle it appropriately
                throw new RuntimeException("Invalid token or user not found");
            }
        } else {
            throw new RuntimeException("Token is required to get user path");
        }
        
        if (Objects.nonNull(path))
            userPath.addAll(path);
        return userPath;        
    }

    public RestFsService(@Qualifier("restFsClient") RestFsClient restFsClient,
            @Qualifier("reactiveRestFsClient") ReactiveRestFsClient reactiveRestFsClient,
            LoginService loginService) {
        this.restFsClient = restFsClient;
        this.reactiveRestFsClient = reactiveRestFsClient;
        this.loginService = loginService;
    }

    @BrokerOperation("listFiles")
    public FsListResponse listFiles(@BrokerParam("token") String token, @BrokerParam("path") List<String> path) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.listFiles(alias, path);
    }

    @BrokerOperation("changeDirectory")
    public Map<String, Object> changeDirectory(@BrokerParam("token") String token, @BrokerParam("path") List<String> path) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.changeDirectory(alias, path);
    }

    @BrokerOperation("createDirectory")
    public Map<String, Object> createDirectory(@BrokerParam("token") String token, @BrokerParam("path") List<String> path) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.createDirectory(alias, path);
    }

    @BrokerOperation("removeDirectory")
    public Map<String, Object> removeDirectory(@BrokerParam("token") String token, @BrokerParam("path") List<String> path) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.removeDirectory(alias, path);
    }

    @BrokerOperation("createFile")
    public Map<String, Object> createFile(@BrokerParam("token") String token, @BrokerParam("path") List<String> path,
            @BrokerParam("filename") String filename) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.createFile(alias, path, filename);
    }

    @BrokerOperation("deleteFile")
    public Map<String, Object> deleteFile(@BrokerParam("token") String token, @BrokerParam("path") List<String> path,
            @BrokerParam("filename") String filename) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.deleteFile(alias, path, filename);
    }

    @BrokerOperation("rename")
    public Map<String, Object> rename(@BrokerParam("token") String token, @BrokerParam("path") List<String> path,
            @BrokerParam("newName") String newName) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.rename(alias, path, newName);
    }

    @BrokerOperation("copy")
    public Map<String, Object> copy(@BrokerParam("fromToken") String fromToken, @BrokerParam("fromPath") List<String> fromPath, 
            @BrokerParam("toToken") String toToken, @BrokerParam("toPath") List<String> toPath) {
        var fromUserResponse = loginService.getUserRegistrationForToken(fromToken);
        var toUserResponse = loginService.getUserRegistrationForToken(toToken);
        if (!fromUserResponse.isOk() || fromUserResponse.getData() == null) {
            throw new RuntimeException("Invalid 'from' token or user not found");
        }
        if (!toUserResponse.isOk() || toUserResponse.getData() == null) {
            throw new RuntimeException("Invalid 'to' token or user not found");
        }
        String fromAlias = fromUserResponse.getData().getAlias();
        String toAlias = toUserResponse.getData().getAlias();
        return restFsClient.copy(fromAlias, fromPath, toAlias, toPath);
    }

    @BrokerOperation("hasFile")
    public Map<String, Object> hasFile(@BrokerParam("token") String token, @BrokerParam("path") List<String> path,
            @BrokerParam("filename") String filename) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.hasFile(alias, path, filename);
    }

    @BrokerOperation("hasFolder")
    public Map<String, Object> hasFolder(@BrokerParam("token") String token, @BrokerParam("path") List<String> path,
            @BrokerParam("foldername") String foldername) {
        var userResponse = loginService.getUserRegistrationForToken(token);
        if (!userResponse.isOk() || userResponse.getData() == null) {
            throw new RuntimeException("Invalid token or user not found");
        }
        String alias = userResponse.getData().getAlias();
        return restFsClient.hasFolder(alias, path, foldername);
    }

}