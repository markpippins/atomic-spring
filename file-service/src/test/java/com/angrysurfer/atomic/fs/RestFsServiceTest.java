package com.angrysurfer.atomic.fs;

import com.angrysurfer.atomic.broker.Broker;
import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.user.UserRegistrationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestFsServiceTest {

    @Mock
    private RestFsClient restFsClient;

    @Mock
    private ReactiveRestFsClient reactiveRestFsClient;

    @Mock
    private Broker broker;

    private RestFsService restFsService;

    @BeforeEach
    void setUp() {
        restFsService = new RestFsService(restFsClient, reactiveRestFsClient, broker);
    }

    @Test
    void testListFilesSuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "user");
        Map<String, Object> mockResponse = Map.of("items", Arrays.asList(Map.of("name", "file1.txt", "type", "file")));
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.listFiles(anyString(), anyList())).thenReturn(mockResponse);

        // Act
        var result = restFsService.listFiles(token, path);

        // Assert
        assertNotNull(result);
        verify(restFsClient).listFiles(anyString(), eq(path));
    }

    @Test
    void testListFilesWithInvalidToken() {
        // Arrange
        String token = "invalid-token";
        List<String> path = Arrays.asList("home", "user");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.error(List.of(), "test-id"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> restFsService.listFiles(token, path));
        
        assertTrue(exception.getMessage().contains("Invalid token or user not found"));
    }

    @Test
    void testChangeDirectorySuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "user");
        Map<String, Object> mockResponse = Map.of("path", "home/user");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.changeDirectory(anyString(), anyList())).thenReturn(mockResponse);

        // Act
        var result = restFsService.changeDirectory(token, path);

        // Assert
        assertNotNull(result);
        verify(restFsClient).changeDirectory(anyString(), eq(path));
    }

    @Test
    void testCreateDirectorySuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "new-dir");
        Map<String, Object> mockResponse = Map.of("created", "home/new-dir");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.createDirectory(anyString(), anyList())).thenReturn(mockResponse);

        // Act
        var result = restFsService.createDirectory(token, path);

        // Assert
        assertNotNull(result);
        verify(restFsClient).createDirectory(anyString(), eq(path));
    }

    @Test
    void testRemoveDirectorySuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "old-dir");
        Map<String, Object> mockResponse = Map.of("deleted", "home/old-dir");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.removeDirectory(anyString(), anyList())).thenReturn(mockResponse);

        // Act
        var result = restFsService.removeDirectory(token, path);

        // Assert
        assertNotNull(result);
        verify(restFsClient).removeDirectory(anyString(), eq(path));
    }

    @Test
    void testCreateFileSuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "user");
        String filename = "newfile.txt";
        Map<String, Object> mockResponse = Map.of("created_file", "home/user/newfile.txt");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.createFile(anyString(), anyList(), anyString())).thenReturn(mockResponse);

        // Act
        var result = restFsService.createFile(token, path, filename);

        // Assert
        assertNotNull(result);
        verify(restFsClient).createFile(anyString(), eq(path), eq(filename));
    }

    @Test
    void testDeleteFileSuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "user");
        String filename = "oldfile.txt";
        Map<String, Object> mockResponse = Map.of("deleted_file", "home/user/oldfile.txt");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.deleteFile(anyString(), anyList(), anyString())).thenReturn(mockResponse);

        // Act
        var result = restFsService.deleteFile(token, path, filename);

        // Assert
        assertNotNull(result);
        verify(restFsClient).deleteFile(anyString(), eq(path), eq(filename));
    }

    @Test
    void testRenameSuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "user", "oldname.txt");
        String newName = "newname.txt";
        Map<String, Object> mockResponse = Map.of("renamed", "old", "to", "new");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.rename(anyString(), anyList(), anyString())).thenReturn(mockResponse);

        // Act
        var result = restFsService.rename(token, path, newName);

        // Assert
        assertNotNull(result);
        verify(restFsClient).rename(anyString(), eq(path), eq(newName));
    }

    @Test
    void testCopySuccess() {
        // Arrange
        String fromToken = "from-token";
        List<String> fromPath = Arrays.asList("home", "from");
        String toToken = "to-token";
        List<String> toPath = Arrays.asList("home", "to");
        String fromAlias = "fromUser";
        String toAlias = "toUser";
        Map<String, Object> mockResponse = Map.of("copied", "from", "to", "to");
        
        when(broker.submit(any(ServiceRequest.class))).thenReturn(
            ServiceResponse.ok(new UserRegistrationDTO() {{ setAlias(fromAlias); }}, "test-id"),
            ServiceResponse.ok(new UserRegistrationDTO() {{ setAlias(toAlias); }}, "test-id")
        );
        when(restFsClient.copy(anyString(), anyList(), anyString(), anyList())).thenReturn(mockResponse);

        // Act
        var result = restFsService.copy(fromToken, fromPath, toToken, toPath);

        // Assert
        assertNotNull(result);
        verify(restFsClient).copy(eq(fromAlias), eq(fromPath), eq(toAlias), eq(toPath));
    }

    @Test
    void testHasFileSuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "user");
        String filename = "test.txt";
        Map<String, Object> mockResponse = Map.of("exists", true, "type", "file");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.hasFile(anyString(), anyList(), anyString())).thenReturn(mockResponse);

        // Act
        var result = restFsService.hasFile(token, path, filename);

        // Assert
        assertNotNull(result);
        verify(restFsClient).hasFile(anyString(), eq(path), eq(filename));
    }

    @Test
    void testHasFolderSuccess() {
        // Arrange
        String token = "test-token";
        List<String> path = Arrays.asList("home", "user");
        String folderName = "testFolder";
        Map<String, Object> mockResponse = Map.of("exists", true, "type", "directory");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.hasFolder(anyString(), anyList(), anyString())).thenReturn(mockResponse);

        // Act
        var result = restFsService.hasFolder(token, path, folderName);

        // Assert
        assertNotNull(result);
        verify(restFsClient).hasFolder(anyString(), eq(path), eq(folderName));
    }

    @Test
    void testMoveItemsSuccess() {
        // Arrange
        String token = "test-token";
        List<String> sourcePath = Arrays.asList("home", "ai", ".space");
        List<String> destPath = Arrays.asList("home", "ai");
        List<Map<String, Object>> items = Arrays.asList(
            Map.of("name", "def.json", "type", "file")
        );
        Map<String, Object> mockResponse = Map.of("moved", "items");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(ServiceResponse.ok(mockResponse, "test-id"));
        when(restFsClient.moveItems(anyString(), anyList(), anyList(), anyList())).thenReturn(mockResponse);

        // Act
        var result = restFsService.moveItems(token, sourcePath, destPath, items);

        // Assert
        assertNotNull(result);
        verify(restFsClient).moveItems(anyString(), eq(sourcePath), eq(destPath), eq(items));
    }

    @Test
    void testGetUserAliasFromTokenSuccess() {
        // Arrange
        String token = "valid-token";
        String expectedAlias = "testUser";
        UserRegistrationDTO userRegistration = new UserRegistrationDTO();
        userRegistration.setAlias(expectedAlias);
        ServiceResponse<UserRegistrationDTO> response = ServiceResponse.ok(userRegistration, "test-id");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(response);

        // Use reflection or test the private method indirectly
        String resultAlias = null;
        try {
            // Call the private method through reflection for testing purposes
            java.lang.reflect.Method method = RestFsService.class.getDeclaredMethod("getUserAliasFromToken", String.class);
            method.setAccessible(true);
            resultAlias = (String) method.invoke(restFsService, token);
        } catch (Exception e) {
            fail("Error calling private method: " + e.getMessage());
        }

        // Assert
        assertEquals(expectedAlias, resultAlias);
    }

    @Test
    void testGetUserAliasFromTokenFailure() {
        // Arrange
        String token = "invalid-token";
        ServiceResponse<UserRegistrationDTO> response = ServiceResponse.error(List.of(), "test-id");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(response);

        // Use reflection to test the private method
        String resultAlias = null;
        try {
            java.lang.reflect.Method method = RestFsService.class.getDeclaredMethod("getUserAliasFromToken", String.class);
            method.setAccessible(true);
            resultAlias = (String) method.invoke(restFsService, token);
        } catch (Exception e) {
            fail("Error calling private method: " + e.getMessage());
        }

        // Assert
        assertNull(resultAlias);
    }

    @Test
    void testGetUserPath() {
        // Test the getUserPath method indirectly by testing methods that use it
        // Since it's private, we test its effect through other methods that use it
        String token = "valid-token";
        List<String> path = Arrays.asList("home", "user");
        
        UserRegistrationDTO userRegistration = new UserRegistrationDTO();
        userRegistration.setAlias("testUser");
        ServiceResponse<UserRegistrationDTO> response = ServiceResponse.ok(userRegistration, "test-id");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(response);
        
        Map<String, Object> mockResponse = Map.of("items", Arrays.asList(Map.of("name", "test.txt", "type", "file")));
        when(restFsClient.listFiles(anyString(), anyList())).thenReturn(mockResponse);

        // Act
        var result = restFsService.listFiles(token, path);

        // Assert
        assertNotNull(result);
        verify(restFsClient).listFiles(eq("testUser"), eq(path));
    }

    @Test
    void testNullToken() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> restFsService.listFiles(null, Arrays.asList("home", "user")));
        
        assertTrue(exception.getMessage().contains("Token is required"));
    }

    @Test
    void testNullPath() {
        // Arrange
        String token = "test-token";
        Map<String, Object> mockResponse = Map.of("items", Arrays.asList(Map.of("name", "test.txt", "type", "file")));
        UserRegistrationDTO userRegistration = new UserRegistrationDTO();
        userRegistration.setAlias("testUser");
        ServiceResponse<UserRegistrationDTO> response = ServiceResponse.ok(userRegistration, "test-id");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(response);
        when(restFsClient.listFiles(anyString(), anyList())).thenReturn(mockResponse);

        // Act
        var result = restFsService.listFiles(token, null);

        // Assert
        assertNotNull(result);
        verify(restFsClient).listFiles(anyString(), eq(null));
    }
}