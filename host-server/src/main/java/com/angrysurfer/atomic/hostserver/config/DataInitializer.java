package com.angrysurfer.atomic.hostserver.config;

import com.angrysurfer.atomic.hostserver.entity.*;
import com.angrysurfer.atomic.hostserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
    
    @Override
    public void run(String... args) {
        initializeFrameworks();
        initializeServers();
        initializeServices();
        initializeDeployments();
        initializeConfigurations();
    }
    
    private void initializeFrameworks() {
        // Java Frameworks
        Framework springBoot = new Framework();
        springBoot.setName("Spring Boot");
        springBoot.setDescription("Java framework for building production-ready applications");
        springBoot.setCategory(Framework.FrameworkCategory.JAVA_SPRING);
        springBoot.setLanguage("Java");
        springBoot.setLatestVersion("3.5.0");
        springBoot.setDocumentationUrl("https://spring.io/projects/spring-boot");
        springBoot.setSupportsBrokerPattern(true);
        frameworkRepository.save(springBoot);
        
        Framework quarkus = new Framework();
        quarkus.setName("Quarkus");
        quarkus.setDescription("Kubernetes-native Java framework");
        quarkus.setCategory(Framework.FrameworkCategory.JAVA_QUARKUS);
        quarkus.setLanguage("Java");
        quarkus.setLatestVersion("3.15.1");
        quarkus.setDocumentationUrl("https://quarkus.io");
        quarkus.setSupportsBrokerPattern(true);
        frameworkRepository.save(quarkus);
        
        Framework micronaut = new Framework();
        micronaut.setName("Micronaut");
        micronaut.setDescription("Modern JVM-based framework for microservices");
        micronaut.setCategory(Framework.FrameworkCategory.JAVA_MICRONAUT);
        micronaut.setLanguage("Java");
        micronaut.setLatestVersion("4.0.0");
        micronaut.setDocumentationUrl("https://micronaut.io");
        micronaut.setSupportsBrokerPattern(false);
        frameworkRepository.save(micronaut);
        
        // Node.js Frameworks
        Framework nestjs = new Framework();
        nestjs.setName("NestJS");
        nestjs.setDescription("Progressive Node.js framework for building server-side applications");
        nestjs.setCategory(Framework.FrameworkCategory.NODE_NESTJS);
        nestjs.setLanguage("TypeScript");
        nestjs.setLatestVersion("10.0.0");
        nestjs.setDocumentationUrl("https://nestjs.com");
        nestjs.setSupportsBrokerPattern(false);
        frameworkRepository.save(nestjs);
        
        Framework adonisjs = new Framework();
        adonisjs.setName("AdonisJS");
        adonisjs.setDescription("Node.js MVC framework");
        adonisjs.setCategory(Framework.FrameworkCategory.NODE_ADONISJS);
        adonisjs.setLanguage("TypeScript");
        adonisjs.setLatestVersion("6.0.0");
        adonisjs.setDocumentationUrl("https://adonisjs.com");
        adonisjs.setSupportsBrokerPattern(false);
        frameworkRepository.save(adonisjs);
        
        Framework moleculer = new Framework();
        moleculer.setName("Moleculer");
        moleculer.setDescription("Progressive microservices framework for Node.js");
        moleculer.setCategory(Framework.FrameworkCategory.NODE_MOLECULER);
        moleculer.setLanguage("TypeScript");
        moleculer.setLatestVersion("0.14.0");
        moleculer.setDocumentationUrl("https://moleculer.services");
        moleculer.setSupportsBrokerPattern(true);
        frameworkRepository.save(moleculer);
    }
    
    private void initializeServers() {
        Host localhost = new Host();
        localhost.setHostname("localhost");
        localhost.setIpAddress("127.0.0.1");
        localhost.setType(Host.ServerType.VIRTUAL);
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
        
        // Spring Boot Services
        Service brokerGateway = createService("broker-gateway", "Main API gateway with service orchestration", 
            springBoot, Service.ServiceType.GATEWAY, 8080, "/api/broker");
        
        Service userService = createService("user-service", "Primary user management with MongoDB", 
            springBoot, Service.ServiceType.REST_API, 8083, "/api/users");
        
        Service loginService = createService("login-service", "Authentication and session management", 
            springBoot, Service.ServiceType.REST_API, 8082, "/api/login");
        
        Service fileService = createService("file-service", "File handling services", 
            springBoot, Service.ServiceType.REST_API, 4040, "/api/files");
        
        Service noteService = createService("note-service", "User notes management", 
            springBoot, Service.ServiceType.REST_API, 8084, "/api/notes");
        
        // Quarkus Services
        Service brokerGatewayQuarkus = createService("broker-gateway-quarkus", "Quarkus implementation of broker gateway", 
            quarkus, Service.ServiceType.GATEWAY, 8190, "/api/broker");
        
        // Moleculer Services
        Service moleculerSearch = createService("moleculer-search", "Search service with multiple providers", 
            moleculer, Service.ServiceType.REST_API, 4050, "/api/search");
        
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
                                  Service.ServiceType type, Integer port, String apiPath) {
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
