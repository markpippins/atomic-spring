package com.angrysurfer.atomic.hostserver.config;

import com.angrysurfer.atomic.hostserver.entity.*;
import com.angrysurfer.atomic.hostserver.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ServiceRepository serviceRepository;
    private final FrameworkRepository frameworkRepository;
    private final FrameworkCategoryRepository categoryRepository;
    private final FrameworkLanguageRepository languageRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServerTypeRepository serverTypeRepository;
    private final EnvironmentTypeRepository environmentTypeRepository;
    private final HostRepository hostRepository;
    private final DeploymentRepository deploymentRepository;
    private final ServiceConfigurationRepository configurationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataInitializer(
            ServiceRepository serviceRepository,
            FrameworkRepository frameworkRepository,
            FrameworkCategoryRepository categoryRepository,
            FrameworkLanguageRepository languageRepository,
            ServiceTypeRepository serviceTypeRepository,
            ServerTypeRepository serverTypeRepository,
            EnvironmentTypeRepository environmentTypeRepository,
            HostRepository hostRepository,
            DeploymentRepository deploymentRepository,
            ServiceConfigurationRepository configurationRepository) {
        this.serviceRepository = serviceRepository;
        this.frameworkRepository = frameworkRepository;
        this.categoryRepository = categoryRepository;
        this.languageRepository = languageRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.serverTypeRepository = serverTypeRepository;
        this.environmentTypeRepository = environmentTypeRepository;
        this.hostRepository = hostRepository;
        this.deploymentRepository = deploymentRepository;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");
        try {
            initializeLookupTables();
            initializeFrameworks();
            initializeServers();
            initializeServices();
            initializeServiceDependencies();
            initializeDeployments();
            initializeConfigurations();
            log.info("Data initialization completed");
        } catch (Exception e) {
            log.error("Data initialization failed", e);
            // Don't re-throw to allow application to start
        }
    }

    private <T> T loadJsonConfig(String resourcePath, TypeReference<T> typeRef) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, typeRef);
        }
    }

    @Transactional
    private void initializeLookupTables() {
        try {
            // Environment Types
            List<Map<String, String>> environmentTypes = loadJsonConfig("config/environment-types.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : environmentTypes) {
                String name = data.get("name");
                if (environmentTypeRepository.findByName(name).isEmpty()) {
                    EnvironmentType environmentType = new EnvironmentType();
                    environmentType.setName(name);
                    environmentType.setActiveFlag(true);
                    environmentTypeRepository.save(environmentType);
                }
            }

            // Service Types
            List<Map<String, String>> serviceTypes = loadJsonConfig("config/service-types.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : serviceTypes) {
                String name = data.get("name");
                if (serviceTypeRepository.findByName(name).isEmpty()) {
                    ServiceType serviceType = new ServiceType();
                    serviceType.setName(name);
                    serviceType.setActiveFlag(true);
                    serviceTypeRepository.save(serviceType);
                }
            }

            // Server Types
            List<Map<String, String>> serverTypes = loadJsonConfig("config/server-types.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : serverTypes) {
                String name = data.get("name");
                if (serverTypeRepository.findByName(name).isEmpty()) {
                    ServerType serverType = new ServerType();
                    serverType.setName(name);
                    serverType.setActiveFlag(true);
                    serverTypeRepository.save(serverType);
                }
            }

            // Framework Categories
            List<Map<String, String>> categories = loadJsonConfig("config/framework-categories.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : categories) {
                String name = data.get("name");
                if (categoryRepository.findByName(name).isEmpty()) {
                    FrameworkCategory category = new FrameworkCategory();
                    category.setName(name);
                    category.setActiveFlag(true);
                    categoryRepository.save(category);
                }
            }

            // Framework Languages
            List<Map<String, String>> languages = loadJsonConfig("config/framework-languages.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : languages) {
                String name = data.get("name");
                if (languageRepository.findByName(name).isEmpty()) {
                    FrameworkLanguage language = new FrameworkLanguage();
                    language.setName(name);
                    language.setDescription(data.get("description"));
                    language.setActiveFlag(true);
                    languageRepository.save(language);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load lookup tables", e);
        }
    }

    @Transactional
    private void initializeFrameworks() {
        try {
            List<Map<String, Object>> frameworks = loadJsonConfig("config/frameworks.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            for (Map<String, Object> data : frameworks) {
                String name = (String) data.get("name");
                if (frameworkRepository.findByName(name).isPresent()) {
                    continue;
                }

                String categoryName = (String) data.get("category");
                String languageName = (String) data.get("language");

                Optional<FrameworkCategory> categoryOpt = categoryRepository.findByName(categoryName);
                Optional<FrameworkLanguage> languageOpt = languageRepository.findByName(languageName);

                if (categoryOpt.isPresent() && languageOpt.isPresent()) {
                    Framework framework = new Framework();
                    framework.setName(name);
                    framework.setDescription((String) data.get("description"));
                    framework.setVendorId(1L); // Default vendor ID
                    framework.setCategoryId(categoryOpt.get().getId());
                    framework.setLanguageId(languageOpt.get().getId());
                    framework.setCurrentVersion((String) data.get("current_version"));
                    framework.setLtsVersion((String) data.get("lts_version"));
                    framework.setUrl((String) data.get("url"));
                    framework.setActiveFlag(true);

                    frameworkRepository.save(framework);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load framework configurations", e);
        }
    }

    @Transactional
    private void initializeServers() {
        try {
            Map<String, Object> data = loadJsonConfig("config/servers.json", new TypeReference<Map<String, Object>>() {
            });

            String hostname = (String) data.get("hostname");
            if (hostRepository.findByHostname(hostname).isPresent()) {
                return;
            }

            String typeName = (String) data.get("type");
            String environmentName = (String) data.get("environment");

            Optional<ServerType> serverTypeOpt = serverTypeRepository.findByName(typeName);
            Optional<EnvironmentType> environmentTypeOpt = environmentTypeRepository.findByName(environmentName);

            if (serverTypeOpt.isPresent() && environmentTypeOpt.isPresent()) {
                Host host = new Host();
                host.setHostname(hostname);
                host.setIpAddress((String) data.get("ipAddress"));
                host.setServerTypeId(serverTypeOpt.get().getId());
                host.setEnvironmentTypeId(environmentTypeOpt.get().getId());
                // For now, using placeholder values for operating system
                host.setOperatingSystemId(1L); // Placeholder - in a real implementation, this would be an OS ID
                host.setCpuCores((Integer) data.get("cpuCores"));
                host.setMemory(data.get("memoryMb") + " MB");
                host.setDisk(data.get("diskGb") + " GB");
                host.setStatus((String) data.get("status"));
                host.setDescription((String) data.get("description"));
                host.setActiveFlag(true);

                hostRepository.save(host);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load servers", e);
        }
    }

    @Transactional
    private void initializeServices() {
        try {
            List<Map<String, Object>> services = loadJsonConfig("config/services.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            for (Map<String, Object> data : services) {
                String name = (String) data.get("name");
                if (serviceRepository.findByName(name).isPresent()) {
                    continue;
                }

                String frameworkName = (String) data.get("framework");
                String typeName = (String) data.get("type");

                Optional<Framework> frameworkOpt = frameworkRepository.findByName(frameworkName);
                Optional<ServiceType> typeOpt = serviceTypeRepository.findByName(typeName);

                if (typeOpt.isEmpty() && typeName.contains("_")) {
                    typeOpt = serviceTypeRepository.findByName(typeName.replace("_", " "));
                }

                if (frameworkOpt.isPresent() && typeOpt.isPresent()) {
                    Service service = new Service();
                    service.setName(name);
                    service.setDescription((String) data.get("description"));
                    service.setFrameworkId(frameworkOpt.get().getId());
                    service.setServiceTypeId(typeOpt.get().getId());
                    service.setDefaultPort((Integer) data.get("defaultPort"));
                    service.setApiBasePath((String) data.get("apiBasePath"));
                    service.setRepositoryUrl((String) data.get("repositoryUrl"));
                    service.setStatus((String) data.getOrDefault("status", "ACTIVE"));
                    service.setVersion((String) data.getOrDefault("version", "1.0.0"));
                    service.setActiveFlag(true);

                    serviceRepository.save(service);
                    log.info("Created Service: {}", name);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load services", e);
        }
    }

    @Transactional
    private void initializeServiceDependencies() {
        try {
            List<Map<String, Object>> services = loadJsonConfig("config/services.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            for (Map<String, Object> data : services) {
                String sourceName = (String) data.get("name");
                List<String> dependencyNames = (List<String>) data.get("dependencies");
                if (dependencyNames == null || dependencyNames.isEmpty())
                    continue;

                Optional<Service> sourceServiceOpt = serviceRepository.findByName(sourceName);
                if (sourceServiceOpt.isEmpty())
                    continue;
                Long sourceId = sourceServiceOpt.get().getId();

                for (String targetName : dependencyNames) {
                    Optional<Service> targetServiceOpt = serviceRepository.findByName(targetName);
                    if (targetServiceOpt.isPresent()) {
                        Long targetId = targetServiceOpt.get().getId();

                        // For now, skip dependency creation since ServiceDependencyRepository doesn't
                        // exist
                        // In a real implementation, you'd create the dependency record
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize dependencies", e);
        }
    }

    @Transactional
    private void initializeDeployments() {
        try {
            List<Map<String, Object>> deployments = loadJsonConfig("config/deployments.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            for (Map<String, Object> data : deployments) {
                String serviceName = (String) data.get("serviceName");
                String hostname = (String) data.get("hostname");

                Optional<Service> serviceOpt = serviceRepository.findByName(serviceName);
                Optional<Host> serverOpt = hostRepository.findByHostname(hostname);

                if (serviceOpt.isPresent() && serverOpt.isPresent()) {
                    // For now, we'll use a placeholder environment ID (DEVELOPMENT environment
                    // type)
                    Optional<EnvironmentType> envTypeOpt = environmentTypeRepository.findByName("DEVELOPMENT");
                    if (envTypeOpt.isEmpty()) {
                        continue;
                    }

                    Service service = serviceOpt.get();

                    Deployment deployment = new Deployment();
                    deployment.setServiceId(service.getId());
                    deployment.setServerId(serverOpt.get().getId());
                    deployment.setEnvironmentId(envTypeOpt.get().getId());
                    deployment.setVersion((String) data.get("version"));
                    deployment.setDeployedAt(LocalDateTime.now());
                    deployment.setActiveFlag(true);

                    // Set the new fields
                    deployment.setPort(service.getDefaultPort() != null ? service.getDefaultPort() : 8080);
                    deployment.setContextPath(service.getApiBasePath() != null ? service.getApiBasePath() : "/");
                    deployment.setHealthStatus("HEALTHY");
                    deployment.setStatus("RUNNING");
                    deployment.setStartedAt(LocalDateTime.now().minusHours(1));
                    deployment.setLastHealthCheck(LocalDateTime.now());

                    // Build health check URL
                    String healthCheckUrl = String.format("http://%s:%d%s/actuator/health",
                            serverOpt.get().getHostname(),
                            deployment.getPort(),
                            deployment.getContextPath());
                    deployment.setHealthCheckUrl(healthCheckUrl);

                    deploymentRepository.save(deployment);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load deployments", e);
        }
    }

    @Transactional
    private void initializeConfigurations() {
        try {
            List<Map<String, Object>> configs = loadJsonConfig("config/service-configurations.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            for (Map<String, Object> data : configs) {
                String serviceName = (String) data.get("serviceName");

                Optional<Service> serviceOpt = serviceRepository.findByName(serviceName);

                if (serviceOpt.isPresent()) {
                    // For config type and environment, use placeholder IDs for now
                    ServiceConfiguration config = new ServiceConfiguration();
                    config.setServiceId(serviceOpt.get().getId());
                    config.setConfigTypeId(1L); // Default config type ID
                    config.setEnvironmentId(1L); // Default environment ID
                    config.setConfigKey((String) data.get("configKey"));
                    config.setConfigValue((String) data.get("configValue"));
                    config.setDescription((String) data.get("description"));
                    config.setActiveFlag(true);

                    configurationRepository.save(config);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configurations", e);
        }
    }
}