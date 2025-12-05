package com.angrysurfer.atomic.hostserver.config;

import com.angrysurfer.atomic.hostserver.entity.*;
import com.angrysurfer.atomic.hostserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
    
    @Override
    public void run(String... args) {
        initializeLookupTables();
        initializeFrameworks();
        initializeServers();
        // initializeServices();
        // initializeDeployments();
        // initializeConfigurations();
    }

    private void initializeLookupTables() {
        // Service Types
        createServiceType("REST_API", "RESTful API Service");
        createServiceType("GRAPHQL_API", "GraphQL API Service");
        createServiceType("GRPC_SERVICE", "gRPC Service");
        createServiceType("MESSAGE_QUEUE", "Message Queue / Broker");
        createServiceType("DATABASE", "Database Service");
        createServiceType("CACHE", "Cache Service");
        createServiceType("GATEWAY", "API Gateway");
        createServiceType("PROXY", "Reverse Proxy");
        createServiceType("WEB_APP", "Web Application");
        createServiceType("BACKGROUND_JOB", "Background Job / Worker");

        // Server Types
        createServerType("PHYSICAL", "Physical Server");
        createServerType("VIRTUAL", "Virtual Machine");
        createServerType("CONTAINER", "Docker Container");
        createServerType("CLOUD", "Cloud Instance");

        // Framework Categories
        createFrameworkCategory("JAVA_SPRING", "Java Spring Framework");
        createFrameworkCategory("JAVA_QUARKUS", "Java Quarkus Framework");
        createFrameworkCategory("JAVA_MICRONAUT", "Java Micronaut Framework");
        createFrameworkCategory("NODE_EXPRESS", "Node.js Express");
        createFrameworkCategory("NODE_NESTJS", "Node.js NestJS");
        createFrameworkCategory("NODE_ADONISJS", "Node.js AdonisJS");
        createFrameworkCategory("NODE_MOLECULER", "Node.js Moleculer");
        createFrameworkCategory("PYTHON_DJANGO", "Python Django");
        createFrameworkCategory("PYTHON_FLASK", "Python Flask");
        createFrameworkCategory("PYTHON_FASTAPI", "Python FastAPI");
        createFrameworkCategory("DOTNET_ASPNET", ".NET ASP.NET Core");
        createFrameworkCategory("GO_GIN", "Go Gin");
        createFrameworkCategory("RUST_ACTIX", "Rust Actix");
        createFrameworkCategory("OTHER", "Other Framework");

        // Framework Languages
        createFrameworkLanguage("Java", "Java Programming Language");
        createFrameworkLanguage("TypeScript", "TypeScript Programming Language");
        createFrameworkLanguage("JavaScript", "JavaScript Programming Language");
        createFrameworkLanguage("Python", "Python Programming Language");
        createFrameworkLanguage("C#", "C# Programming Language");
        createFrameworkLanguage("Go", "Go Programming Language");
        createFrameworkLanguage("Rust", "Rust Programming Language");
    }
    
    private void initializeFrameworks() {
        FrameworkCategory javaSpring = frameworkCategoryRepository.findByName("JAVA_SPRING").orElseThrow();
        FrameworkCategory javaQuarkus = frameworkCategoryRepository.findByName("JAVA_QUARKUS").orElseThrow();
        FrameworkCategory javaMicronaut = frameworkCategoryRepository.findByName("JAVA_MICRONAUT").orElseThrow();
        FrameworkCategory nodeNestjs = frameworkCategoryRepository.findByName("NODE_NESTJS").orElseThrow();
        FrameworkCategory nodeAdonisjs = frameworkCategoryRepository.findByName("NODE_ADONISJS").orElseThrow();
        FrameworkCategory nodeMoleculer = frameworkCategoryRepository.findByName("NODE_MOLECULER").orElseThrow();

        FrameworkLanguage java = frameworkLanguageRepository.findByName("Java").orElseThrow();
        FrameworkLanguage typeScript = frameworkLanguageRepository.findByName("TypeScript").orElseThrow();

        // Java Frameworks
        createFramework("Spring Boot", "Java framework for building production-ready applications", 
            javaSpring, java, "3.5.0", "https://spring.io/projects/spring-boot", true);
        
        createFramework("Quarkus", "Kubernetes-native Java framework", 
            javaQuarkus, java, "3.15.1", "https://quarkus.io", true);
        
        createFramework("Micronaut", "Modern JVM-based framework for microservices", 
            javaMicronaut, java, "4.0.0", "https://micronaut.io", false);
        
        // Node.js Frameworks
        createFramework("NestJS", "Progressive Node.js framework for building server-side applications", 
            nodeNestjs, typeScript, "10.0.0", "https://nestjs.com", false);
        
        createFramework("AdonisJS", "Node.js MVC framework", 
            nodeAdonisjs, typeScript, "6.0.0", "https://adonisjs.com", false);
        
        createFramework("Moleculer", "Progressive microservices framework for Node.js", 
            nodeMoleculer, typeScript, "0.14.0", "https://moleculer.services", true);
    }
    
    private void initializeServers() {
        ServerType virtual = serverTypeRepository.findByName("VIRTUAL").orElseThrow();

        Host localhost = new Host();
        localhost.setHostname("localhost");
        localhost.setIpAddress("127.0.0.1");
        localhost.setType(virtual);
        localhost.setEnvironment(Host.ServerEnvironment.DEVELOPMENT);
        localhost.setOperatingSystem("Windows 11");
        localhost.setCpuCores(8);
        localhost.setMemoryMb(16384L);
        localhost.setDiskGb(512L);
        localhost.setStatus(Host.ServerStatus.ACTIVE);
        localhost.setDescription("Local development machine");
        hostRepository.save(localhost);
    }
    
    private void initializeServices() {
        Framework springBoot = frameworkRepository.findByName("Spring Boot").orElse(null);
        Framework quarkus = frameworkRepository.findByName("Quarkus").orElse(null);
        Framework moleculer = frameworkRepository.findByName("Moleculer").orElse(null);

        ServiceType gateway = serviceTypeRepository.findByName("GATEWAY").orElseThrow();
        ServiceType restApi = serviceTypeRepository.findByName("REST_API").orElseThrow();
        
        // Spring Boot Services
        Service brokerGateway = createService("broker-gateway", "Main API gateway with service orchestration", 
            springBoot, gateway, 8080, "/api/broker");
        
        Service userService = createService("user-service", "Primary user management with MongoDB", 
            springBoot, restApi, 8083, "/api/users");
        
        Service loginService = createService("login-service", "Authentication and session management", 
            springBoot, restApi, 8082, "/api/login");
        
        Service fileService = createService("file-service", "File handling services", 
            springBoot, restApi, 4040, "/api/files");
        
        Service noteService = createService("note-service", "User notes management", 
            springBoot, restApi, 8084, "/api/notes");
        
        // Quarkus Services
        Service brokerGatewayQuarkus = createService("broker-gateway-quarkus", "Quarkus implementation of broker gateway", 
            quarkus, gateway, 8190, "/api/broker");
        
        // Moleculer Services
        Service moleculerSearch = createService("moleculer-search", "Search service with multiple providers", 
            moleculer, restApi, 4050, "/api/search");
        
        // Add dependencies
        loginService.getDependencies().add(userService);
        noteService.getDependencies().add(loginService);
        brokerGateway.getDependencies().add(userService);
        brokerGateway.getDependencies().add(loginService);
        brokerGateway.getDependencies().add(fileService);
        
        serviceRepository.save(loginService);
        serviceRepository.save(noteService);
        serviceRepository.save(brokerGateway);
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
        Host localhost = hostRepository.findByHostname("localhost").orElse(null);
        Service brokerGateway = serviceRepository.findByName("broker-gateway").orElse(null);
        Service userService = serviceRepository.findByName("user-service").orElse(null);
        
        if (localhost != null && brokerGateway != null) {
            Deployment deployment = new Deployment();
            deployment.setService(brokerGateway);
            deployment.setServer(localhost);
            deployment.setPort(8080);
            deployment.setVersion("1.0.0");
            deployment.setStatus(Deployment.DeploymentStatus.RUNNING);
            deployment.setEnvironment(Deployment.DeploymentEnvironment.DEVELOPMENT);
            deployment.setHealthCheckUrl("http://localhost:8080/actuator/health");
            deployment.setHealthStatus(Deployment.HealthStatus.HEALTHY);
            deploymentRepository.save(deployment);
        }
        
        if (localhost != null && userService != null) {
            Deployment deployment = new Deployment();
            deployment.setService(userService);
            deployment.setServer(localhost);
            deployment.setPort(8083);
            deployment.setVersion("1.0.0");
            deployment.setStatus(Deployment.DeploymentStatus.RUNNING);
            deployment.setEnvironment(Deployment.DeploymentEnvironment.DEVELOPMENT);
            deployment.setHealthCheckUrl("http://localhost:8083/actuator/health");
            deployment.setHealthStatus(Deployment.HealthStatus.HEALTHY);
            deploymentRepository.save(deployment);
        }
    }
    
    private void initializeConfigurations() {
        Service brokerGateway = serviceRepository.findByName("broker-gateway").orElse(null);
        
        if (brokerGateway != null) {
            ServiceConfiguration config1 = new ServiceConfiguration();
            config1.setService(brokerGateway);
            config1.setConfigKey("spring.data.mongodb.uri");
            config1.setConfigValue("mongodb://localhost:27017/broker");
            config1.setEnvironment(ServiceConfiguration.ConfigEnvironment.DEVELOPMENT);
            config1.setType(ServiceConfiguration.ConfigType.DATABASE_URL);
            config1.setIsSecret(false);
            config1.setDescription("MongoDB connection string for development");
            configurationRepository.save(config1);
            
            ServiceConfiguration config2 = new ServiceConfiguration();
            config2.setService(brokerGateway);
            config2.setConfigKey("server.port");
            config2.setConfigValue("8080");
            config2.setEnvironment(ServiceConfiguration.ConfigEnvironment.ALL);
            config2.setType(ServiceConfiguration.ConfigType.NUMBER);
            config2.setIsSecret(false);
            config2.setDescription("Server port");
            configurationRepository.save(config2);
        }
    }
}
