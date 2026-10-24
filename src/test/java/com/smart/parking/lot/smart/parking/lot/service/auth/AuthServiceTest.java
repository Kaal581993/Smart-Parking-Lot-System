package com.smart.parking.lot.smart.parking.lot.service.auth;

import com.smart.parking.lot.smart.parking.lot.entity.auth.Privilege;
import com.smart.parking.lot.smart.parking.lot.entity.auth.Role;
import com.smart.parking.lot.smart.parking.lot.repository.auth.AppUserRepository;
import com.smart.parking.lot.smart.parking.lot.repository.auth.PrivilegeRepository;
import com.smart.parking.lot.smart.parking.lot.repository.auth.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void createRolePersistsEverySelectedPrivilege() {
        Privilege readAddresses = new Privilege(1L, "READ_ADDRESSES");
        Privilege createAddresses = new Privilege(2L, "CREATE_ADDRESSES");
        when(roleRepository.findByName("ADDRESS_EDITOR")).thenReturn(Optional.empty());
        when(privilegeRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(readAddresses, createAddresses));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role role = authService.createRole("ADDRESS_EDITOR", Set.of(1L, 2L));

        assertEquals(Set.of(readAddresses, createAddresses), role.getPrivileges());
    }

    @Test
    void updateRoleRejectsAPrivilegeThatDoesNotExist() {
        Role role = new Role(7L, "ADDRESS_EDITOR", new java.util.HashSet<>());
        when(roleRepository.findById(7L)).thenReturn(Optional.of(role));
        when(privilegeRepository.findAllById(Set.of(99L))).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> authService.updateRole(7L, null, Set.of(99L)));
    }
}
