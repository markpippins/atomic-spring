package com.angrysurfer.atomic.hostserver.config;

import com.angrysurfer.atomic.hostserver.entity.*;
import com.angrysurfer.atomic.hostserver.repository.*;
import com.angrysurfer.atomic.hostserver.service.CacheWarmingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

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
    private final LibraryCategoryRepository libraryCategoryRepository;
    private final LibraryRepository libraryRepository;
    private final OperatingSystemRepository operatingSystemRepository;
    private final FrameworkVendorRepository frameworkVendorRepository;
    private final CacheWarmingService cacheWarmingService;
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
            ServiceConfigurationRepository configurationRepository,
            LibraryCategoryRepository libraryCategoryRepository,
            LibraryRepository libraryRepository,
            OperatingSystemRepository operatingSystemRepository,
            FrameworkVendorRepository frameworkVendorRepository,
            CacheWarmingService cacheWarmingService) {
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
        this.libraryCategoryRepository = libraryCategoryRepository;
        this.libraryRepository = libraryRepository;
        this.operatingSystemRepository = operatingSystemRepository;
        this.frameworkVendorRepository = frameworkVendorRepository;
        this.cacheWarmingService = cacheWarmingService;
    }

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");
        try {
            // Clear all caches on startup to ensure no stale data interferes with
            // initialization
            log.info("Clearing Redis caches before initialization...");
            cacheWarmingService.clearAllCaches();

            // Initialize foundational data first
            initializeEnvironmentTypes();
            initializeServiceTypes();
            initializeServerTypes();
            initializeFrameworkCategories();
            initializeFrameworkLanguages();
            initializeFrameworkVendors();

            // Initialize data that depends on the foundational data
            initializeFrameworks();
            initializeLibraryCategories();
            initializeLibraries();
            initializeOperatingSystems();
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
        log.info("Loading configuration from: {}", resourcePath);
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {
            T loadedData = objectMapper.readValue(inputStream, typeRef);
            if (loadedData instanceof List) {
                log.info("Loaded {} items from {}", ((List<?>) loadedData).size(), resourcePath);
            }
            return loadedData;
        } catch (IOException e) {
            log.error("Failed to load configuration from {}: {}", resourcePath, e.getMessage());
            throw e;
        }
    }

    private void initializeEnvironmentTypes() {
        try {
            List<Map<String, String>> environmentTypes = loadJsonConfig("config/environment-types.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : environmentTypes) {
                try {
                    String name = data.get("name");
                    if (environmentTypeRepository.findByName(name).isEmpty()) {
                        EnvironmentType environmentType = new EnvironmentType();
                        environmentType.setName(name);
                        environmentType.setActiveFlag(true);
                        environmentTypeRepository.save(environmentType);
                        log.info("Created EnvironmentType: {}", name);
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize environment type: " + data.get("name"), e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load environment types config", e);
        }
    }

    private void initializeServiceTypes() {
        try {
            List<Map<String, String>> serviceTypes = loadJsonConfig("config/service-types.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : serviceTypes) {
                try {
                    String name = data.get("name");
                    if (serviceTypeRepository.findByName(name).isEmpty()) {
                        ServiceType serviceType = new ServiceType();
                        serviceType.setName(name);
                        serviceType.setActiveFlag(true);
                        serviceTypeRepository.save(serviceType);
                        log.info("Created ServiceType: {}", name);
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize service type: " + data.get("name"), e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load service types config", e);
        }
    }

    private void initializeServerTypes() {
        try {
            List<Map<String, String>> serverTypes = loadJsonConfig("config/server-types.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : serverTypes) {
                try {
                    String name = data.get("name");
                    if (serverTypeRepository.findByName(name).isEmpty()) {
                        ServerType serverType = new ServerType();
                        serverType.setName(name);
                        serverType.setActiveFlag(true);
                        serverTypeRepository.save(serverType);
                        log.info("Created ServerType: {}", name);
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize server type: " + data.get("name"), e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load server types config", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initializeFrameworkCategories() {
        try {
            List<Map<String, String>> categories = loadJsonConfig("config/framework-categories.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : categories) {
                try {
                    String name = data.get("name");
                    if (categoryRepository.findByName(name).isEmpty()) {
                        FrameworkCategory category = new FrameworkCategory();
                        category.setName(name);
                        category.setActiveFlag(true);
                        categoryRepository.save(category);
                        log.info("Created FrameworkCategory: {}", name);
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize framework category: " + data.get("name"), e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load framework categories config", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initializeFrameworkLanguages() {
        try {
            List<Map<String, String>> languages = loadJsonConfig("config/framework-languages.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : languages) {
                try {
                    String name = data.get("name");
                    if (languageRepository.findByName(name).isEmpty()) {
                        FrameworkLanguage language = new FrameworkLanguage();
                        language.setName(name);
                        language.setDescription(data.get("description"));
                        language.setActiveFlag(true);
                        languageRepository.save(language);
                        log.info("Created FrameworkLanguage: {}", name);
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize framework language: " + data.get("name"), e);
                }

            }
        } catch (IOException e) {
            log.error("Failed to load framework languages config", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initializeFrameworkVendors() {
        try {
            List<Map<String, String>> vendors = loadJsonConfig("config/framework-vendors.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : vendors) {
                try {
                    String name = data.get("name");
                    if (frameworkVendorRepository.findByName(name).isEmpty()) {
                        FrameworkVendor vendor = new FrameworkVendor();
                        vendor.setName(name);
                        vendor.setDescription(data.get("description"));
                        vendor.setActiveFlag(true);
                        frameworkVendorRepository.save(vendor);
                        log.info("Created FrameworkVendor: {}", name);
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize framework vendor: " + data.get("name"), e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load framework vendors config", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initializeFrameworks() {
        try {
            List<Map<String, Object>> frameworks = loadJsonConfig("config/frameworks.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            int createdCount = 0;
            int updatedCount = 0;
            int skippedCount = 0;

            for (Map<String, Object> data : frameworks) {
                try {
                    String name = (String) data.get("name");
                    String categoryName = (String) data.get("category");
                    String languageName = (String) data.get("language");

                    Optional<FrameworkCategory> categoryOpt = categoryRepository.findByName(categoryName);
                    Optional<FrameworkLanguage> languageOpt = languageRepository.findByName(languageName);
                    Optional<FrameworkVendor> vendorOpt = frameworkVendorRepository.findByName("FOSS");

                    if (categoryOpt.isEmpty()) {
                        log.warn("Category '{}' not found for framework '{}', skipping", categoryName, name);
                        skippedCount++;
                        continue;
                    }

                    if (languageOpt.isEmpty()) {
                        log.warn("Language '{}' not found for framework '{}', skipping", languageName, name);
                        skippedCount++;
                        continue;
                    }

                    Optional<Framework> existingOpt = frameworkRepository.findByName(name);
                    Framework framework;
                    boolean isNew = false;

                    if (existingOpt.isPresent()) {
                        framework = existingOpt.get();
                    } else {
                        framework = new Framework();
                        framework.setName(name);
                        isNew = true;
                    }

                    framework.setDescription((String) data.get("description"));
                    vendorOpt.ifPresent(v -> framework.setVendorId(v.getId()));
                    framework.setCategoryId(categoryOpt.get().getId());
                    framework.setLanguageId(languageOpt.get().getId());
                    framework.setCurrentVersion((String) data.get("current_version"));
                    framework.setLtsVersion((String) data.get("lts_version"));
                    framework.setUrl((String) data.get("url"));
                    framework.setActiveFlag(true);

                    frameworkRepository.save(framework);

                    if (isNew) {
                        log.info("Created Framework: {}", name);
                        createdCount++;
                    } else {
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize framework: " + data.get("name"), e);
                }
            }
            log.info("Framework initialization completed: {} created, {} updated, {} skipped",
                    createdCount, updatedCount, skippedCount);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load framework configurations", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initializeServers() {
        try {
            List<Map<String, Object>> servers = loadJsonConfig("config/servers.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            int createdCount = 0;
            int updatedCount = 0;
            int skippedCount = 0;

            for (Map<String, Object> data : servers) {
                try {
                    String hostname = (String) data.get("hostname");
                    String typeName = (String) data.get("type");
                    String environmentName = (String) data.get("environment");
                    String osName = (String) data.get("operatingSystem");

                    Optional<ServerType> serverTypeOpt = serverTypeRepository.findByName(typeName);
                    Optional<EnvironmentType> environmentTypeOpt = environmentTypeRepository
                            .findByName(environmentName);

                    if (serverTypeOpt.isEmpty()) {
                        log.warn("Server Type '{}' not found for server '{}', skipping", typeName, hostname);
                        skippedCount++;
                        continue;
                    }

                    if (environmentTypeOpt.isEmpty()) {
                        log.warn("Environment Type '{}' not found for server '{}', skipping", environmentName,
                                hostname);
                        skippedCount++;
                        continue;
                    }

                    Long osId = 1L;
                    if (osName != null) {
                        Optional<OperatingSystem> osOpt = operatingSystemRepository.findByName(osName);
                        if (osOpt.isPresent()) {
                            osId = osOpt.get().getId();
                        } else {
                            log.warn("Operating System '{}' not found for server '{}', defaulting to ID 1", osName,
                                    hostname);
                        }
                    }

                    Optional<Host> existingOpt = hostRepository.findByHostname(hostname);
                    Host host;
                    boolean isNew = false;

                    if (existingOpt.isPresent()) {
                        host = existingOpt.get();
                    } else {
                        host = new Host();
                        host.setHostname(hostname);
                        isNew = true;
                    }

                    host.setIpAddress((String) data.get("ipAddress"));
                    host.setServerTypeId(serverTypeOpt.get().getId());
                    host.setEnvironmentTypeId(environmentTypeOpt.get().getId());
                    host.setOperatingSystemId(osId);
                    host.setCpuCores((Integer) data.get("cpuCores"));
                    host.setMemory(data.get("memoryMb") + " MB");
                    host.setDisk(data.get("diskGb") + " GB");
                    host.setStatus((String) data.get("status"));
                    host.setDescription((String) data.get("description"));
                    host.setActiveFlag(true);

                    hostRepository.save(host);

                    if (isNew) {
                        log.info("Created Server: {}", hostname);
                        createdCount++;
                    } else {
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize server: " + data.get("hostname"), e);
                }
            }
            log.info("Server initialization completed: {} created, {} updated, {} skipped",
                    createdCount, updatedCount, skippedCount);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load servers", e);
        }
    }

    @Transactional
    private void initializeOperatingSystems() {
        try {
            List<Map<String, String>> osList = loadJsonConfig("config/operating-systems.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : osList) {
                String name = data.get("name");
                if (operatingSystemRepository.findByName(name).isEmpty()) {
                    OperatingSystem os = new OperatingSystem();
                    os.setName(name);
                    os.setDescription(data.get("description"));
                    os.setVersion(data.get("version"));
                    os.setFamily(data.get("family"));
                    os.setActiveFlag(true);
                    operatingSystemRepository.save(os);
                    log.info("Created Operating System: {}", name);
                }
            }
        } catch (IOException e) {
            log.warn("Failed to load operating systems config", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initializeServices() {
        try {
            List<Map<String, Object>> services = loadJsonConfig("config/services.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            int createdCount = 0;
            int updatedCount = 0;
            int skippedCount = 0;

            for (Map<String, Object> data : services) {
                try {
                    String name = (String) data.get("name");
                    String frameworkName = (String) data.get("framework");
                    String typeName = (String) data.get("type");

                    Optional<Framework> frameworkOpt = frameworkRepository.findByName(frameworkName);
                    Optional<ServiceType> typeOpt = serviceTypeRepository.findByName(typeName);

                    if (typeOpt.isEmpty() && typeName.contains("_")) {
                        typeOpt = serviceTypeRepository.findByName(typeName.replace("_", " "));
                    }

                    if (frameworkOpt.isEmpty()) {
                        log.warn("Framework '{}' not found for service '{}', skipping", frameworkName, name);
                        skippedCount++;
                        continue;
                    }

                    if (typeOpt.isEmpty()) {
                        log.warn("Service type '{}' not found for service '{}', skipping", typeName, name);
                        skippedCount++;
                        continue;
                    }

                    Optional<Service> existingServiceOpt = serviceRepository.findByName(name);
                    Service service;
                    boolean isNew = false;

                    if (existingServiceOpt.isPresent()) {
                        service = existingServiceOpt.get();
                        log.debug("Updating existing service: {}", name);
                    } else {
                        service = new Service();
                        service.setName(name);
                        isNew = true;
                        log.debug("Creating new service: {}", name);
                    }

                    // Update service fields from JSON
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

                    if (isNew) {
                        log.info("Created Service: {}", name);
                        createdCount++;
                    } else {
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to sync service: " + data.get("name"), e);
                }
            }

            log.info("Service initialization completed: {} created, {} updated, {} skipped",
                    createdCount, updatedCount, skippedCount);
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
                // Long sourceId = sourceServiceOpt.get().getId();

                for (String targetName : dependencyNames) {
                    Optional<Service> targetServiceOpt = serviceRepository.findByName(targetName);
                    if (targetServiceOpt.isPresent()) {
                        // Long targetId = targetServiceOpt.get().getId();

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

    @Transactional
    private void initializeLibraryCategories() {
        try {
            List<Map<String, String>> categories = loadJsonConfig("config/library-categories.json",
                    new TypeReference<List<Map<String, String>>>() {
                    });
            for (Map<String, String> data : categories) {
                String name = data.get("name");
                if (libraryCategoryRepository.findByName(name).isEmpty()) {
                    LibraryCategory category = new LibraryCategory();
                    category.setName(name);
                    category.setDescription(data.get("description"));
                    category.setActiveFlag(true);
                    libraryCategoryRepository.save(category);
                    log.info("Created LibraryCategory: {}", name);
                }
            }
        } catch (IOException e) {
            log.warn("Failed to load library categories config, skipping: {}", e.getMessage());
        }
    }

    @Transactional
    private void initializeLibraries() {
        try {
            List<Map<String, Object>> libraries = loadJsonConfig("config/libraries.json",
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            for (Map<String, Object> data : libraries) {
                String name = (String) data.get("name");
                if (libraryRepository.findByName(name).isPresent()) {
                    continue;
                }

                String categoryName = (String) data.get("category");
                String languageName = (String) data.get("language");

                Optional<LibraryCategory> categoryOpt = libraryCategoryRepository.findByName(categoryName);
                Optional<FrameworkLanguage> languageOpt = languageRepository.findByName(languageName);

                Library library = new Library();
                library.setName(name);
                library.setDescription((String) data.get("description"));
                library.setCurrentVersion((String) data.get("current_version"));
                library.setPackageName((String) data.get("package_name"));
                library.setPackageManager((String) data.get("package_manager"));
                library.setUrl((String) data.get("url"));
                library.setRepositoryUrl((String) data.get("repository_url"));
                library.setLicense((String) data.get("license"));
                library.setActiveFlag(true);

                categoryOpt.ifPresent(cat -> library.setCategoryId(cat.getId()));
                languageOpt.ifPresent(lang -> library.setLanguageId(lang.getId()));

                libraryRepository.save(library);
                log.info("Created Library: {}", name);
            }
        } catch (IOException e) {
            log.warn("Failed to load libraries config, skipping: {}", e.getMessage());
        }
    }
}