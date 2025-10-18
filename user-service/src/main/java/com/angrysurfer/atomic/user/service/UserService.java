package com.angrysurfer.atomic.user.service;

import com.angrysurfer.atomic.user.UserDTO;
import com.angrysurfer.atomic.user.model.Profile;
import com.angrysurfer.atomic.user.model.User;
import com.angrysurfer.atomic.user.repository.ProfileRepository;
import com.angrysurfer.atomic.user.repository.UserRepository;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.user.ResourceNotFoundException;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userService")
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Record representing a test user with basic information. Used for
     * demonstration and testing purposes.
     */
    public record TestUser(Long id, String email, String alias) {

        // Record body is intentionally empty as all functionality
        // is provided by the record's automatic implementation
    }

    /**
     * Record representing a user creation request. Contains validation
     * annotations for input validation.
     */
    public record CreateUserReq(
            @Email String email,
            @NotBlank String alias
    ) {

        // Record body is intentionally empty as all functionality
        // is provided by the record's automatic implementation
    }

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    public UserService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        log.info("UserService initialized");
    }

   @BrokerOperation("login")
   public UserDTO login(@BrokerParam("alias") String alias, @BrokerParam("identifier") String password) {

       log.info("Login user {}", alias);
       User user = userRepository.findByAlias(alias).orElse(null);

       if (user == null || !user.getIdentifier().equals(password)) {
           return null;
       }

       return user.toDTO();
   }

    @BrokerOperation("createUser")
    public UserDTO createUser(@BrokerParam("email") String email,
                              @BrokerParam("alias") String alias,
                              @BrokerParam("identifier") String password) {

        log.info("Create user {}", email);

        User user = new User(alias, email, null);
        user.setIdentifier(password);

        userRepository.save(user);
        return user.toDTO();
    }

    @BrokerOperation("delete")
    public void delete(@BrokerParam("userId") Long userId) {
        log.info("Delete user id {}", userId);
        userRepository.deleteById(userId);
    }

    @BrokerOperation("findAll")
    public Set<UserDTO> findAll() {
        log.info("Find all users");
        HashSet<User> result = new HashSet<>();
        userRepository.findAll().forEach(result::add);
        return result.stream().map(user -> user.toDTO()).collect(Collectors.toSet());
    }

    @BrokerOperation("findById")
    public UserDTO findById(@BrokerParam("userId") Long userId) throws ResourceNotFoundException {
        log.info("Find user by id {}", userId);
        Optional<User> result = userRepository.findById(userId);
        if (result.isPresent()) {
            return result.get().toDTO();
        }

        throw new ResourceNotFoundException("User ".concat(Long.toString(userId).concat(" not found.")));
    }

    @BrokerOperation("findByAlias")
    public UserDTO findByAlias(@BrokerParam("alias") String alias) throws ResourceNotFoundException {
        log.info("Find user by alias {}", alias);
        UserDTO result;

        Optional<User> user = userRepository.findByAlias(alias);
        if (user.isPresent()) {
            Optional<Profile> profile = profileRepository.findByUserId(user.get().getId());
            if (profile.isPresent()) {
                user.get().setProfile(profile.get());
                result = user.get().toDTO();
            } else {
                result = user.get().toDTO();
            }

            return result;
        }

        throw new ResourceNotFoundException("User ".concat(alias).concat(" not found."));
    }

    @BrokerOperation("findByEmail")
    public UserDTO findByEmail(@BrokerParam("email") String email) throws ResourceNotFoundException {
        log.info("Find user by email {}", email);
        UserDTO result;

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            Optional<Profile> profile = profileRepository.findByUserId(user.get().getId());
            if (profile.isPresent()) {
                result = user.get().toDTO();
            } else {
                result = user.get().toDTO();
            }

            return result;
        }
        throw new ResourceNotFoundException("User ".concat(email).concat(" not found."));
    }

    @BrokerOperation("addUser")
    public UserDTO addUser(@BrokerParam("user") UserDTO newUser) {
        log.info("Adding user {}", newUser.getAlias());
        User user = new User(newUser.getAlias(), newUser.getEmail(), newUser.getAvatarUrl());
        if (newUser.getIdentifier() != null) {
            user.setIdentifier(newUser.getIdentifier());
        }
        return userRepository.save(user).toDTO();
    }

    public UserDTO save(@BrokerParam("alias") String alias, @BrokerParam("email") String email, @BrokerParam("password") String initialPassword) {
        log.info("Save user {}", alias);
        return userRepository.save(new User(alias, email, null)).toDTO();
    }

    @BrokerOperation("save")
    public UserDTO save(UserDTO newUser) {
        log.info("Save user {}", newUser.getAlias());
        User user = new User(newUser.getAlias(), newUser.getEmail(), newUser.getAvatarUrl());
        if (newUser.getIdentifier() != null) {
            user.setIdentifier(newUser.getIdentifier());
        }
        return userRepository.save(user).toDTO();
    }

    @BrokerOperation("update")
    public UserDTO update(@BrokerParam("user") User user) {
        log.info("Update user {}", user.getAlias());
        return userRepository.save(user).toDTO();
    }

    @BrokerOperation("getApplications")
    public List<Application> getApplications() {
        return List.of(new Application("App 1", "1"), new Application("App 2", "2"));
    }

    /**
     * Record representing an application with name and ID.
     */
    public record Application(String name, String id) {

        // Record body is intentionally empty as all functionality
        // is provided by the record's automatic implementation
    }

    @BrokerOperation("getUser")
    public UserDTO getUser(@BrokerParam("id") Long id) throws ResourceNotFoundException {
        return findById(id);
    }

    @BrokerOperation("deleteUser")
    public void deleteUser(@BrokerParam("id") Long id) {
        delete(id);
    }

    @BrokerOperation("saveUser")
    public UserDTO saveUser(@BrokerParam("user") UserDTO user) {
        return save(user);
    }

    @BrokerOperation("getUsers")
    public Set<UserDTO> getUsers() {
        return findAll();
    }

    // @BrokerOperation("getChoices")
    // public List<Choice> getChoices() {
    //     return List.of(new Choice("Choice 1", "1"), new Choice("Choice 2", "2"));
    // }

    // /**
    //  * Record representing a choice with label and value.
    //  */
    // public record Choice(String label, String value) {

    //     // Record body is intentionally empty as all functionality
    //     // is provided by the record's automatic implementation
    // }
}
