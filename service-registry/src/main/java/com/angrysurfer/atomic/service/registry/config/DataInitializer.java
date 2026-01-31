package com.angrysurfer.atomic.service.registry.config;

import com.angrysurfer.atomic.service.registry.entity.*;
import com.angrysurfer.atomic.service.registry.repository.*;
import com.angrysurfer.atomic.service.registry.service.CacheWarmingService;
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
    private final ServiceConfigTypeRepository serviceConfigTypeRepository;
    private final ServiceDependencyRepository serviceDependencyRepository;
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
            ServiceConfigTypeRepository serviceConfigTypeRepository,
            ServiceDependencyRepository serviceDependencyRepository,
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
        this.serviceConfigTypeRepository = serviceConfigTypeRepository;
        this.serviceDependencyRepository = serviceDependencyRepository;
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

            // Initialize foundational data first (no dependencies)
            initializeEnvironmentTypes();
            initializeServiceTypes();
            initializeServerTypes();
            initializeFrameworkCategories();
            initializeFrameworkLanguages();
            initializeFrameworkVendors();
            initializeLibraryCategories();
            initializeOperatingSystems();

            // Verify foundational data exists before proceeding
            validateFoundationalData();

            // Initialize data that depends on foundational data
            initializeFrameworks();
            initializeLibraries();

            // Verify frameworks exist before initializing dependent data
            validateFrameworkData();

            // Initialize infrastructure data
            initializeServers();

            // Initialize services (depends on frameworks)
            initializeServices();

            // Initialize higher-level data that depends on services/servers
            initializeServiceDependencies(); // depends on services
            initializeDeployments();         // depends on services and servers
            initializeConfigurations();      // depends on services

            log.info("Data initialization completed");
        } catch (Exception e) {
            log.error("Data initialization failed", e);
            // Don't re-throw to allow application to start
        }
    }

    private void validateFoundationalData() {
        long envTypeCount = environmentTypeRepository.count();
        long serviceTypeCount = serviceTypeRepository.count();
        long serverTypeCount = serverTypeRepository.count();
        long categoryCount = categoryRepository.count();
        long languageCount = languageRepository.count();
        long vendorCount = frameworkVendorRepository.count();

        log.info("Validating foundational data: Environment Types={}, Service Types={}, Server Types={}, Categories={}, Languages={}, Vendors={}",
                envTypeCount, serviceTypeCount, serverTypeCount, categoryCount, languageCount, vendorCount);

        if (envTypeCount == 0) {
            log.warn("No environment types found - this may cause issues with server and deployment initialization");
        }
        if (serviceTypeCount == 0) {
            log.warn("No service types found - this may cause issues with service initialization");
        }
        if (categoryCount == 0) {
            log.warn("No framework categories found - this may cause issues with framework initialization");
        }
        if (languageCount == 0) {
            log.warn("No framework languages found - this may cause issues with framework initialization");
        }
        if (vendorCount == 0) {
            log.warn("No framework vendors found - this may cause issues with framework initialization");
        }
    }

    private void validateFrameworkData() {
        long frameworkCount = frameworkRepository.count();
        log.info("Validating framework data: Frameworks={}", frameworkCount);

        if (frameworkCount == 0) {
            log.warn("No frameworks found - this will cause issues with service initialization");
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

    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Transactional
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
                    // Validate required fields first
                    String name = (String) data.get("name");
                    if (name == null || name.trim().isEmpty()) {
                        log.warn("Framework name is missing or empty in configuration, skipping");
                        skippedCount++;
                        continue;
                    }

                    String categoryName = (String) data.get("category");
                    if (categoryName == null || categoryName.trim().isEmpty()) {
                        log.warn("Category name is missing for framework '{}', skipping", name);
                        skippedCount++;
                        continue;
                    }

                    String languageName = (String) data.get("language");
                    if (languageName == null || languageName.trim().isEmpty()) {
                        log.warn("Language name is missing for framework '{}', skipping", name);
                        skippedCount++;
                        continue;
                    }

                    Optional<FrameworkCategory> categoryOpt = categoryRepository.findByName(categoryName);
                    Optional<FrameworkLanguage> languageOpt = languageRepository.findByName(languageName);

                    // Try to find the vendor from the data, default to "FOSS" if not specified, or create "Unknown" if "FOSS" doesn't exist
                    String vendorName = (String) data.get("vendor");
                    if (vendorName == null) {
                        vendorName = "FOSS";
                    }
                    Optional<FrameworkVendor> vendorOpt = frameworkVendorRepository.findByName(vendorName);

                    // If the specific vendor isn't found, try FOSS as fallback, then Unknown
                    if (vendorOpt.isEmpty()) {
                        log.debug("Vendor '{}' not found for framework '{}', trying FOSS as fallback", vendorName, name);
                        vendorOpt = frameworkVendorRepository.findByName("FOSS");
                    }
                    if (vendorOpt.isEmpty()) {
                        log.debug("FOSS vendor not found for framework '{}', trying Unknown as fallback", name);
                        vendorOpt = frameworkVendorRepository.findByName("Unknown");
                    }

                    if (categoryOpt.isEmpty()) {
                        log.warn("Category '{}' not found for framework '{}', available categories: {}",
                                categoryName, name,
                                categoryRepository.findAll().stream().map(FrameworkCategory::getName).toList());
                        skippedCount++;
                        continue;
                    }

                    if (languageOpt.isEmpty()) {
                        log.warn("Language '{}' not found for framework '{}', available languages: {}",
                                languageName, name,
                                languageRepository.findAll().stream().map(FrameworkLanguage::getName).toList());
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
                    framework.setCategoryId(categoryOpt.get().getId());
                    framework.setLanguageId(languageOpt.get().getId());
                    vendorOpt.ifPresent(v -> framework.setVendorId(v.getId()));
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

    @Transactional
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
                        log.warn("Server Type '{}' not found for server '{}', available server types: {}",
                                typeName, hostname,
                                serverTypeRepository.findAll().stream().map(ServerType::getName).toList());
                        skippedCount++;
                        continue;
                    }

                    if (environmentTypeOpt.isEmpty()) {
                        log.warn("Environment Type '{}' not found for server '{}', available environment types: {}",
                                environmentName, hostname,
                                environmentTypeRepository.findAll().stream().map(EnvironmentType::getName).toList());
                        skippedCount++;
                        continue;
                    }

                    Long osId = null;
                    if (osName != null) {
                        Optional<OperatingSystem> osOpt = operatingSystemRepository.findByName(osName);
                        if (osOpt.isPresent()) {
                            osId = osOpt.get().getId();
                        } else {
                            log.warn("Operating System '{}' not found for server '{}', available operating systems: {}",
                                    osName, hostname,
                                    operatingSystemRepository.findAll().stream().map(OperatingSystem::getName).toList());
                            // Try to find a default OS or skip if none found
                            Optional<OperatingSystem> defaultOsOpt = operatingSystemRepository.findByName("Linux");
                            if (defaultOsOpt.isPresent()) {
                                osId = defaultOsOpt.get().getId();
                                log.info("Using default OS 'Linux' with ID {} for server '{}'", osId, hostname);
                            } else {
                                log.warn("No default OS found for server '{}', skipping OS assignment", hostname);
                            }
                        }
                    }

                    // If still no OS ID found, try to get the first available OS
                    if (osId == null && operatingSystemRepository.count() > 0) {
                        OperatingSystem firstOs = operatingSystemRepository.findAll().iterator().next();
                        osId = firstOs.getId();
                        log.info("Using first available OS '{}' with ID {} for server '{}'",
                                firstOs.getName(), osId, hostname);
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

    @Transactional
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
                    // Validate required fields first
                    String name = (String) data.get("name");
                    if (name == null || name.trim().isEmpty()) {
                        log.warn("Service name is missing or empty in configuration, skipping");
                        skippedCount++;
                        continue;
                    }

                    String frameworkName = (String) data.get("framework");
                    if (frameworkName == null || frameworkName.trim().isEmpty()) {
                        log.warn("Framework name is missing for service '{}', skipping", name);
                        skippedCount++;
                        continue;
                    }

                    String typeName = (String) data.get("type");
                    if (typeName == null || typeName.trim().isEmpty()) {
                        log.warn("Service type is missing for service '{}', skipping", name);
                        skippedCount++;
                        continue;
                    }

                    Optional<Framework> frameworkOpt = frameworkRepository.findByName(frameworkName);
                    Optional<ServiceType> typeOpt = serviceTypeRepository.findByName(typeName);

                    if (typeOpt.isEmpty() && typeName.contains("_")) {
                        typeOpt = serviceTypeRepository.findByName(typeName.replace("_", " "));
                    }

                    if (frameworkOpt.isEmpty()) {
                        log.warn("Framework '{}' not found for service '{}', available frameworks: {}",
                                frameworkName, name,
                                frameworkRepository.findAll().stream().map(Framework::getName).toList());
                        skippedCount++;
                        continue;
                    }

                    if (typeOpt.isEmpty()) {
                        log.warn("Service type '{}' not found for service '{}', available types: {}",
                                typeName, name,
                                serviceTypeRepository.findAll().stream().map(ServiceType::getName).toList());
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

                    // Update service fields from JSON with validation
                    service.setDescription((String) data.get("description"));
                    service.setFrameworkId(frameworkOpt.get().getId());
                    service.setServiceTypeId(typeOpt.get().getId());

                    // Validate and set port
                    Object portObj = data.get("defaultPort");
                    if (portObj != null) {
                        if (portObj instanceof Integer) {
                            service.setDefaultPort((Integer) portObj);
                        } else if (portObj instanceof String) {
                            try {
                                service.setDefaultPort(Integer.parseInt((String) portObj));
                            } catch (NumberFormatException e) {
                                log.warn("Invalid port format for service '{}': {}", name, portObj);
                            }
                        }
                    }

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
                if (sourceServiceOpt.isEmpty()) {
                    log.warn("Source service '{}' not found for dependencies, skipping", sourceName);
                    continue;
                }

                Service sourceService = sourceServiceOpt.get();

                for (String targetName : dependencyNames) {
                    Optional<Service> targetServiceOpt = serviceRepository.findByName(targetName);
                    if (targetServiceOpt.isPresent()) {
                        Service targetService = targetServiceOpt.get();

                        // Check if this dependency already exists to avoid duplicates
                        List<ServiceDependency> existingDependencies = serviceDependencyRepository
                            .findByServiceIdAndTargetServiceId(sourceService.getId(), targetService.getId());

                        if (existingDependencies.isEmpty()) {
                            ServiceDependency dependency = new ServiceDependency();
                            dependency.setServiceId(sourceService.getId());
                            dependency.setTargetServiceId(targetService.getId());
                            dependency.setActiveFlag(true);
                            dependency.setCreatedAt(java.time.LocalDateTime.now());

                            serviceDependencyRepository.save(dependency);
                            log.info("Created dependency: {} -> {}", sourceName, targetName);
                        } else {
                            log.debug("Dependency already exists: {} -> {}", sourceName, targetName);
                        }
                    } else {
                        log.warn("Target service '{}' not found for dependency of '{}', available services: {}",
                                targetName, sourceName,
                                serviceRepository.findAll().stream().map(Service::getName).toList());
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
                    ServiceConfiguration config = new ServiceConfiguration();
                    config.setServiceId(serviceOpt.get().getId());

                    // Try to find appropriate config type and environment from the data
                    String configTypeName = (String) data.get("configType");
                    String environmentName = (String) data.get("environment");

                    Long configTypeId = null;
                    if (configTypeName != null) {
                        Optional<ServiceConfigType> configTypeOpt = serviceConfigTypeRepository.findByName(configTypeName);
                        if (configTypeOpt.isPresent()) {
                            configTypeId = configTypeOpt.get().getId();
                        }
                    }

                    // If no specific config type found, use first available or default
                    if (configTypeId == null && serviceConfigTypeRepository.count() > 0) {
                        ServiceConfigType firstConfigType = serviceConfigTypeRepository.findAll().iterator().next();
                        configTypeId = firstConfigType.getId();
                    }

                    // If still no config type, try to use a default one
                    if (configTypeId == null) {
                        Optional<ServiceConfigType> defaultConfigType = serviceConfigTypeRepository.findByName("DEFAULT");
                        if (defaultConfigType.isPresent()) {
                            configTypeId = defaultConfigType.get().getId();
                        } else {
                            // Create or use first available
                            Iterable<ServiceConfigType> allConfigTypes = serviceConfigTypeRepository.findAll();
                            if (allConfigTypes.iterator().hasNext()) {
                                configTypeId = allConfigTypes.iterator().next().getId();
                            } else {
                                log.warn("No config types available for service '{}', skipping configuration", serviceName);
                                continue;
                            }
                        }
                    }

                    Long environmentId = null;
                    if (environmentName != null) {
                        Optional<EnvironmentType> envOpt = environmentTypeRepository.findByName(environmentName);
                        if (envOpt.isPresent()) {
                            environmentId = envOpt.get().getId();
                        }
                    }

                    // If no specific environment found, use DEVELOPMENT as default
                    if (environmentId == null) {
                        Optional<EnvironmentType> devEnvOpt = environmentTypeRepository.findByName("DEVELOPMENT");
                        if (devEnvOpt.isPresent()) {
                            environmentId = devEnvOpt.get().getId();
                        } else {
                            // Use first available environment
                            Iterable<EnvironmentType> allEnvironments = environmentTypeRepository.findAll();
                            if (allEnvironments.iterator().hasNext()) {
                                environmentId = allEnvironments.iterator().next().getId();
                            } else {
                                log.warn("No environments available for service '{}', skipping configuration", serviceName);
                                continue;
                            }
                        }
                    }

                    config.setConfigTypeId(configTypeId);
                    config.setEnvironmentId(environmentId);
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