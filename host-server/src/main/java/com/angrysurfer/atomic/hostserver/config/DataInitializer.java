package com.angrysurfer.atomic.hostserver.config;

import com.angrysurfer.atomic.hostserver.entity.*;
import com.angrysurfer.atomic.hostserver.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FrameworkRepository frameworkRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private DeploymentRepository deploymentRepository;

    @Autowired
    private ServiceConfigurationRepository configurationRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private ServerTypeRepository serverTypeRepository;

    @Autowired
    private FrameworkCategoryRepository frameworkCategoryRepository;

    @Autowired
    private FrameworkLanguageRepository frameworkLanguageRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${datainit.services.broker-gateway.name:spring-broker-gateway}")
    private String brokerGatewayServiceName;

    @Value("${datainit.services.user-service.name:user-service}")
    private String userServiceName;

    @Value("${datainit.services.login-service.name:login-service}")
    private String loginServiceName;

    @Value("${datainit.services.file-service.name:file-service}")
    private String fileServiceName;

    @Value("${datainit.services.note-service.name:note-service}")
    private String noteServiceName;

    @Value("${datainit.services.quarkus-broker-gateway.name:quarkus-broker-gateway}")
    private String quarkusBrokerGatewayServiceName;

    @Value("${datainit.services.moleculer-search.name:moleculer-search}")
    private String moleculerSearchServiceName;

    @Value("${datainit.host.hostname:localhost}")
    private String hostname;

    @Value("${datainit.deployments.spring-broker-gateway.port:8080}")
    private Integer brokerGatewayPort;

    @Value("${datainit.deployments.user-service.port:8083}")
    private Integer userServicePort;

    @Value("${datainit.configurations.broker-gateway.mongodb-uri:mongodb://localhost:27017/broker}")
    private String brokerGatewayMongoDbUri;

    @Value("${datainit.configurations.broker-gateway.server-port:8080}")
    private String brokerGatewayServerPort;
    
    @Override
    public void run(String... args) {
        initializeLookupTables();
        initializeFrameworks();
        initializeServers();
        initializeServices();
        initializeDeployments();
        initializeConfigurations();
    }

    private <T> T loadJsonConfig(String resourcePath, TypeReference<T> typeRef) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, typeRef);
        }
    }

    private void initializeLookupTables() {
        try {
            // Load and process Service Types from JSON
            List<Map<String, String>> serviceTypes = loadJsonConfig("config/service-types.json", new TypeReference<List<Map<String, String>>>() {});
            for (Map<String, String> serviceTypeData : serviceTypes) {
                createServiceType(serviceTypeData.get("name"), serviceTypeData.get("description"));
            }

            // Load and process Server Types from JSON
            List<Map<String, String>> serverTypes = loadJsonConfig("config/server-types.json", new TypeReference<List<Map<String, String>>>() {});
            for (Map<String, String> serverTypeData : serverTypes) {
                createServerType(serverTypeData.get("name"), serverTypeData.get("description"));
            }

            // Load and process Framework Categories from JSON
            List<Map<String, String>> frameworkCategories = loadJsonConfig("config/framework-categories.json", new TypeReference<List<Map<String, String>>>() {});
            for (Map<String, String> frameworkCategoryData : frameworkCategories) {
                createFrameworkCategory(frameworkCategoryData.get("name"), frameworkCategoryData.get("description"));
            }

            // Load and process Framework Languages from JSON
            List<Map<String, String>> frameworkLanguages = loadJsonConfig("config/framework-languages.json", new TypeReference<List<Map<String, String>>>() {});
            for (Map<String, String> frameworkLanguageData : frameworkLanguages) {
                createFrameworkLanguage(frameworkLanguageData.get("name"), frameworkLanguageData.get("description"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load lookup table configurations", e);
        }
    }
    
    private void initializeFrameworks() {
        try {
            List<Map<String, Object>> frameworks = loadJsonConfig("config/frameworks.json", new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> frameworkData : frameworks) {
                String name = (String) frameworkData.get("name");
                String description = (String) frameworkData.get("description");
                String categoryName = (String) frameworkData.get("category");
                String languageName = (String) frameworkData.get("language");
                String version = (String) frameworkData.get("version");
                String url = (String) frameworkData.get("url");
                Boolean supportsBroker = (Boolean) frameworkData.get("supportsBroker");

                FrameworkCategory category = frameworkCategoryRepository.findByName(categoryName).orElseThrow();
                FrameworkLanguage language = frameworkLanguageRepository.findByName(languageName).orElseThrow();

                createFramework(name, description, category, language, version, url, supportsBroker);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load framework configurations", e);
        }
    }
    
    private void initializeServers() {
        try {
            Map<String, Object> serverData = loadJsonConfig("config/servers.json", new TypeReference<Map<String, Object>>() {});

            String hostname = (String) serverData.get("hostname");
            String ipAddress = (String) serverData.get("ipAddress");
            String typeName = (String) serverData.get("type");
            String environmentStr = (String) serverData.get("environment");
            String operatingSystemStr = (String) serverData.get("operatingSystem");
            Integer cpuCores = (Integer) serverData.get("cpuCores");
            Long memoryMb = ((Integer) serverData.get("memoryMb")).longValue();
            Long diskGb = ((Integer) serverData.get("diskGb")).longValue();
            String statusStr = (String) serverData.get("status");
            String description = (String) serverData.get("description");

            ServerType serverType = serverTypeRepository.findByName(typeName).orElseThrow();

            Host host = new Host();
            host.setHostname(hostname);
            host.setIpAddress(ipAddress);
            host.setType(serverType);
            host.setEnvironment(Host.ServerEnvironment.valueOf(environmentStr));
            host.setOperatingSystem(operatingSystemStr);
            host.setCpuCores(cpuCores);
            host.setMemoryMb(memoryMb);
            host.setDiskGb(diskGb);
            host.setStatus(Host.ServerStatus.valueOf(statusStr));
            host.setDescription(description);
            hostRepository.save(host);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load server configurations", e);
        }
    }
    
    private void initializeServices() {
        try {
            List<Map<String, Object>> services = loadJsonConfig("config/services.json", new TypeReference<List<Map<String, Object>>>() {});

            // Create a map of services to be able to reference them by name
            Map<String, Service> serviceMap = new java.util.HashMap<>();

            for (Map<String, Object> serviceData : services) {
                String name = (String) serviceData.get("name");
                String description = (String) serviceData.get("description");
                String frameworkName = (String) serviceData.get("framework");
                String typeName = (String) serviceData.get("type");
                Integer defaultPort = (Integer) serviceData.get("defaultPort");
                String apiBasePath = (String) serviceData.get("apiBasePath");
                List<String> dependencies = (List<String>) serviceData.get("dependencies");

                Framework framework = frameworkRepository.findByName(frameworkName).orElse(null);
                ServiceType type = serviceTypeRepository.findByName(typeName).orElseThrow();

                Service service = createService(name, description, framework, type, defaultPort, apiBasePath);
                serviceMap.put(name, service);
            }

            // Now set up dependencies after all services are created
            for (Map<String, Object> serviceData : services) {
                String name = (String) serviceData.get("name");
                List<String> dependencies = (List<String>) serviceData.get("dependencies");

                Service currentService = serviceMap.get(name);
                for (String dependencyName : dependencies) {
                    Service dependencyService = serviceMap.get(dependencyName);
                    if (dependencyService != null) {
                        currentService.getDependencies().add(dependencyService);
                    }
                }

                // Save the service with its dependencies
                serviceRepository.save(currentService);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load service configurations", e);
        }
    }
    
    private Service createService(String name, String description, Framework framework, 
                                  ServiceType type, Integer port, String apiPath) {
        Service service = new Service();
        service.setName(name);
        service.setDescription(description);
        service.setFramework(framework);
        service.setType(type);
        service.setDefaultPort(port);
        service.setApiBasePath(apiPath);
        service.setHealthCheckPath("/actuator/health");
        service.setStatus(Service.ServiceStatus.ACTIVE);
        service.setVersion("1.0.0");
        return serviceRepository.save(service);
    }

    private void createFramework(String name, String description, FrameworkCategory category, 
                                 FrameworkLanguage language, String version, String url, boolean supportsBroker) {
        Framework framework = new Framework();
        framework.setName(name);
        framework.setDescription(description);
        framework.setCategory(category);
        framework.setLanguage(language);
        framework.setLatestVersion(version);
        framework.setDocumentationUrl(url);
        framework.setSupportsBrokerPattern(supportsBroker);
        frameworkRepository.save(framework);
    }

    private void createServiceType(String name, String description) {
        if (serviceTypeRepository.findByName(name).isEmpty()) {
            ServiceType type = new ServiceType();
            type.setName(name);
            type.setDescription(description);
            serviceTypeRepository.save(type);
        }
    }

    private void createServerType(String name, String description) {
        if (serverTypeRepository.findByName(name).isEmpty()) {
            ServerType type = new ServerType();
            type.setName(name);
            type.setDescription(description);
            serverTypeRepository.save(type);
        }
    }

    private void createFrameworkCategory(String name, String description) {
        if (frameworkCategoryRepository.findByName(name).isEmpty()) {
            FrameworkCategory category = new FrameworkCategory();
            category.setName(name);
            category.setDescription(description);
            frameworkCategoryRepository.save(category);
        }
    }

    private void createFrameworkLanguage(String name, String description) {
        if (frameworkLanguageRepository.findByName(name).isEmpty()) {
            FrameworkLanguage language = new FrameworkLanguage();
            language.setName(name);
            language.setDescription(description);
            frameworkLanguageRepository.save(language);
        }
    }
    
    private void initializeDeployments() {
        try {
            List<Map<String, Object>> deployments = loadJsonConfig("config/deployments.json", new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> deploymentData : deployments) {
                String serviceName = (String) deploymentData.get("serviceName");
                String hostname = (String) deploymentData.get("hostname");
                Integer port = (Integer) deploymentData.get("port");
                String version = (String) deploymentData.get("version");
                String statusStr = (String) deploymentData.get("status");
                String environmentStr = (String) deploymentData.get("environment");
                String healthCheckUrl = (String) deploymentData.get("healthCheckUrl");
                String healthStatusStr = (String) deploymentData.get("healthStatus");

                Service service = serviceRepository.findByName(serviceName).orElse(null);
                Host host = hostRepository.findByHostname(hostname).orElse(null);

                if (host != null && service != null) {
                    Deployment deployment = new Deployment();
                    deployment.setService(service);
                    deployment.setServer(host);
                    deployment.setPort(port);
                    deployment.setVersion(version);
                    deployment.setStatus(Deployment.DeploymentStatus.valueOf(statusStr));
                    deployment.setEnvironment(Deployment.DeploymentEnvironment.valueOf(environmentStr));
                    deployment.setHealthCheckUrl(healthCheckUrl);
                    deployment.setHealthStatus(Deployment.HealthStatus.valueOf(healthStatusStr));
                    deploymentRepository.save(deployment);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load deployment configurations", e);
        }
    }
    
    private void initializeConfigurations() {
        try {
            List<Map<String, Object>> configurations = loadJsonConfig("config/service-configurations.json", new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> configData : configurations) {
                String serviceName = (String) configData.get("serviceName");
                String configKey = (String) configData.get("configKey");
                String configValue = (String) configData.get("configValue");
                String environmentStr = (String) configData.get("environment");
                String typeStr = (String) configData.get("type");
                Boolean isSecret = (Boolean) configData.get("isSecret");
                String description = (String) configData.get("description");

                Service service = serviceRepository.findByName(serviceName).orElse(null);

                if (service != null) {
                    ServiceConfiguration configuration = new ServiceConfiguration();
                    configuration.setService(service);
                    configuration.setConfigKey(configKey);
                    configuration.setConfigValue(configValue);
                    configuration.setEnvironment(ServiceConfiguration.ConfigEnvironment.valueOf(environmentStr));
                    configuration.setType(ServiceConfiguration.ConfigType.valueOf(typeStr));
                    configuration.setIsSecret(isSecret);
                    configuration.setDescription(description);
                    configurationRepository.save(configuration);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load service configuration parameters", e);
        }
    }
}
