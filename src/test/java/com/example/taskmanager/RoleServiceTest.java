package com.example.taskmanager;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.repository.RoleRepository;
import com.example.taskmanager.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Test when Role exists
    @Test
    void getRoleByName_ShouldReturnRole_WhenRoleExists() {
        // Arrange
        Role role = new Role();
        role.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);

        // Act
        Role foundRole = roleService.getRoleByName("ROLE_USER");

        // Assert
        assertNotNull(foundRole);
        assertEquals("ROLE_USER", foundRole.getName());
    }

    //Test when Role does not exist
    @Test
    void getRoleByName_ShouldReturnNull_WhenRoleDoesNotExist() {
        // Arrange
        when(roleRepository.findByName(anyString())).thenReturn(null);

        // Act
        Role foundRole = roleService.getRoleByName("ROLE_NON_EXISTENT");

        // Assert
        assertNull(foundRole);
    }
    //Test on creation if role already exists
    @Test
    void createRole_ShouldThrowException_WhenRoleAlreadyExists() {
        // Arrange
        Role existingRole = new Role();
        existingRole.setName("ROLE_ADMIN");

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(existingRole);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.createRole("ROLE_ADMIN");
        });

        assertEquals("Role already exists", exception.getMessage());
    }
    //Test n creation if role does not exist
    @Test
    void createRole_ShouldSaveNewRole_WhenRoleDoesNotExist() {
        // Arrange
        when(roleRepository.findByName(anyString())).thenReturn(null);

        Role newRole = new Role();
        newRole.setName("ROLE_NEW");

        when(roleRepository.save(any(Role.class))).thenReturn(newRole);

        // Act
        Role savedRole = roleService.createRole("ROLE_NEW");

        // Assert
        assertNotNull(savedRole);
        assertEquals("ROLE_NEW", savedRole.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

}
