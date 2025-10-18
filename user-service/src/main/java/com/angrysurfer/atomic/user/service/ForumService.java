package com.angrysurfer.atomic.user.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.user.ForumDTO;
import com.angrysurfer.atomic.user.model.Forum;
import com.angrysurfer.atomic.user.repository.ForumRepository;

@Service
public class ForumService {

    private static final Logger log = LoggerFactory.getLogger(ForumService.class);
    private final ForumRepository forumRepository;

    public ForumService(ForumRepository forumRepository) {
        this.forumRepository = forumRepository;
        log.info("ForumService initialized");
    }

    @BrokerOperation("delete")
    public ServiceResponse<String> delete(@BrokerParam("forumId") Long forumId) {
        log.info("Delete forum id {}", forumId);
        try {
            forumRepository.deleteById(forumId);
            return ServiceResponse.ok("Forum deleted successfully", "delete-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error deleting forum: {}", e.getMessage());
            return (ServiceResponse<String>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to delete forum: " + e.getMessage())),
                "delete-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("findById")
    public ServiceResponse<ForumDTO> findById(@BrokerParam("forumId") Long forumId) {
        log.info("Find forum by id {}", forumId);
        try {
            Optional<Forum> forum = forumRepository.findById(forumId);
            if (forum.isPresent()) {
                return ServiceResponse.ok(forum.get().toDTO(), "findById-" + System.currentTimeMillis());
            }
            return (ServiceResponse<ForumDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Forum " + forumId + " not found")),
                "findById-" + System.currentTimeMillis()
            );
        } catch (Exception e) {
            log.error("Error finding forum by id: {}", e.getMessage());
            return (ServiceResponse<ForumDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to find forum: " + e.getMessage())),
                "findById-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("findAll")
    public ServiceResponse<Iterable<ForumDTO>> findAll() {
        log.info("Find all forums");
        try {
            Iterable<ForumDTO> forums = forumRepository.findAll().stream()
                .map(forum -> forum.toDTO())
                .collect(Collectors.toSet());
            return ServiceResponse.ok(forums, "findAll-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error finding all forums: {}", e.getMessage());
            return (ServiceResponse<Iterable<ForumDTO>>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to find forums: " + e.getMessage())),
                "findAll-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("save")
    public ServiceResponse<ForumDTO> save(@BrokerParam("name") String name) {
        log.info("Save forum {}", name);
        try {
            ForumDTO result = forumRepository.save(new Forum(name)).toDTO();
            return ServiceResponse.ok(result, "save-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error saving forum: {}", e.getMessage());
            return (ServiceResponse<ForumDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to save forum: " + e.getMessage())),
                "save-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("saveForum")
    public ServiceResponse<ForumDTO> save(@BrokerParam("forum") Forum forum) {
        log.info("Save forum {}", forum.getName());
        try {
            ForumDTO result = forumRepository.save(forum).toDTO();
            return ServiceResponse.ok(result, "saveForum-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error saving forum entity: {}", e.getMessage());
            return (ServiceResponse<ForumDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to save forum entity: " + e.getMessage())),
                "saveForum-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("findByName")
    public ServiceResponse<ForumDTO> findByName(@BrokerParam("name") String name) {
        log.info("Find forum by name {}", name);
        try {
            Optional<Forum> forum = forumRepository.findByName(name);
            if (forum.isPresent()) {
                return ServiceResponse.ok(forum.get().toDTO(), "findByName-" + System.currentTimeMillis());
            }
            return (ServiceResponse<ForumDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Forum " + name + " not found")),
                "findByName-" + System.currentTimeMillis()
            );
        } catch (Exception e) {
            log.error("Error finding forum by name: {}", e.getMessage());
            return (ServiceResponse<ForumDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to find forum by name: " + e.getMessage())),
                "findByName-" + System.currentTimeMillis()
            );
        }
    }
}
