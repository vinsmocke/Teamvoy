package com.teamvoy.task.service;

import com.teamvoy.task.exception.NullEntityReferenceException;
import com.teamvoy.task.model.Role;
import com.teamvoy.task.repository.RoleRepository;
import com.teamvoy.task.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoleSuccess() {
        Role role = new Role(1L, "ROLE_USER");
        when(roleRepository.save(role)).thenReturn(role);

        Role createdRole = roleService.create(role);

        assertNotNull(createdRole);
        assertEquals(role, createdRole);

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testCreateRoleWithNullRole() {
        assertThrows(NullEntityReferenceException.class, () -> roleService.create(null));

        verifyNoInteractions(roleRepository);
    }

    @Test
    void testReadRoleByIdSuccess() {
        Role role = new Role(1L, "ROLE_USER");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role retrievedRole = roleService.readById(1L);

        assertNotNull(retrievedRole);
        assertEquals(role, retrievedRole);

        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void testReadRoleByIdNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.readById(1L));

        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateRoleSuccess() {
        Role existingRole = new Role(1L, "ROLE_USER");
        Role updatedRole = new Role(1L, "ROLE_ADMIN");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(updatedRole)).thenReturn(updatedRole);

        Role modifiedRole = roleService.update(updatedRole);

        assertNotNull(modifiedRole);
        assertEquals(updatedRole, modifiedRole);

        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(updatedRole);
    }

    @Test
    void testUpdateRoleNotFound() {
        Role role = new Role(1L, "ROLE_USER");
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.update(role));

        verify(roleRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void testUpdateRoleWithNullRole() {
        assertThrows(NullEntityReferenceException.class, () -> roleService.update(null));

        verifyNoInteractions(roleRepository);
    }

    @Test
    void testDeleteRoleSuccess() {
        Role role = new Role(1L, "ROLE_USER");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.delete(1L);

        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void testDeleteRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roleService.delete(1L));

        verify(roleRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void testGetAllRoles() {
        List<Role> roleList = new ArrayList<>();
        roleList.add(new Role(1L, "ROLE_USER"));
        roleList.add(new Role(2L, "ROLE_ADMIN"));
        when(roleRepository.findAll()).thenReturn(roleList);

        List<Role> allRoles = roleService.getAll();

        assertNotNull(allRoles);
        assertEquals(roleList.size(), allRoles.size());

        verify(roleRepository, times(1)).findAll();
    }
}
