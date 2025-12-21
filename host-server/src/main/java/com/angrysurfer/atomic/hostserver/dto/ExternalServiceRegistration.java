package com.angrysurfer.atomic.hostserver.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ExternalServiceRegistration {
    private String serviceName;
    private List<String> operations;
    private String endpoint;
    private String healthCheck;
    private Map<String, Object> metadata;
    private String framework;
    private String version;
    private Integer port;
    private List<HostedServiceInfo> hostedServices;

    @Data
    public static class HostedServiceInfo {
        private String serviceName;
        private List<String> operations;
    }
}