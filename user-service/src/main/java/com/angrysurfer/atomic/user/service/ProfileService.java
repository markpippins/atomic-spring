package com.angrysurfer.atomic.user.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.user.ProfileDTO;
import com.angrysurfer.atomic.user.model.Profile;
import com.angrysurfer.atomic.user.model.User;
import com.angrysurfer.atomic.user.repository.ProfileRepository;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
        log.info("ProfileService initialized");
    }

    @BrokerOperation("findByUserId")
    public ServiceResponse<ProfileDTO> findByUserId(@BrokerParam("userId") Long userId) {
        log.info("Find profile by user id {}", userId);
        try {
            Optional<Profile> profile = profileRepository.findByUserId(userId);
            if (profile.isPresent()) {
                return ServiceResponse.ok(profile.get().toDTO(), "findByUserId-" + System.currentTimeMillis());
            }
            return (ServiceResponse<ProfileDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Profile not found for user " + userId)),
                "findByUserId-" + System.currentTimeMillis()
            );
        } catch (Exception e) {
            log.error("Error finding profile by user id: {}", e.getMessage());
            return (ServiceResponse<ProfileDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to find profile: " + e.getMessage())),
                "findByUserId-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("deleteByUserId")
    public ServiceResponse<String> deleteByUserId(@BrokerParam("userId") Long userId) {
        log.info("Delete profile by user id {}", userId);
        try {
            profileRepository.deleteByUserId(userId);
            return ServiceResponse.ok("Profile deleted successfully", "deleteByUserId-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error deleting profile: {}", e.getMessage());
            return (ServiceResponse<String>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to delete profile: " + e.getMessage())),
                "deleteByUserId-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("save")
    public ServiceResponse<ProfileDTO> save(@BrokerParam("user") User user, @BrokerParam("firstName") String firstName, @BrokerParam("lastName") String lastName) {
        log.info("Save profile for user {}", user.getAlias());
        try {
            Profile p = new Profile();
            p.setUser(user);
            p.setFirstName(firstName);
            p.setLastName(lastName);
            
            ProfileDTO result = profileRepository.save(p).toDTO();
            return ServiceResponse.ok(result, "save-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error saving profile: {}", e.getMessage());
            return (ServiceResponse<ProfileDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to save profile: " + e.getMessage())),
                "save-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("createProfile")
    public ServiceResponse<ProfileDTO> createProfile(@BrokerParam("profileData") ProfileDTO profileData) {
        log.info("Create profile from ProfileDTO");
        try {
            Profile p = new Profile();
            p.setFirstName(profileData.getFirstName());
            p.setLastName(profileData.getLastName());
            p.setCity(profileData.getCity());
            p.setState(profileData.getState());
            p.setProfileImageUrl(profileData.getProfileImageUrl());
            
            ProfileDTO result = profileRepository.save(p).toDTO();
            return ServiceResponse.ok(result, "createProfile-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error creating profile: {}", e.getMessage());
            return (ServiceResponse<ProfileDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to create profile: " + e.getMessage())),
                "createProfile-" + System.currentTimeMillis()
            );
        }
    }

}