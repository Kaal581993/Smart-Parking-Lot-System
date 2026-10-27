package com.smart.parking.lot.smart.parking.lot.service.auth;

import com.smart.parking.lot.smart.parking.lot.entity.auth.AppUser;
import com.smart.parking.lot.smart.parking.lot.entity.auth.Privilege;
import com.smart.parking.lot.smart.parking.lot.entity.auth.Role;
import com.smart.parking.lot.smart.parking.lot.repository.auth.AppUserRepository;
import com.smart.parking.lot.smart.parking.lot.repository.auth.PrivilegeRepository;
import com.smart.parking.lot.smart.parking.lot.repository.auth.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.Map;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void seedAdmin() {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ADMIN", new java.util.HashSet<>()));
        }
        if (appUserRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN").get();
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            appUserRepository.save(admin);
        }
    }

    public String login(String username, String password) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, username);
        return token;
    }

    public void logout(String token) {
        tokenStore.remove(token);
    }

    public void invalidateOtherSessions(String username, String currentToken) {
        tokenStore.entrySet().removeIf(entry -> username.equals(entry.getValue()) && !entry.getKey().equals(currentToken));
    }

    public AppUser getUserByToken(String token) {
        String username = tokenStore.get(token);
        if (username == null) return null;
        return appUserRepository.findByUsername(username).orElse(null);
    }

    public AppUser registerOwner(String username, String password, String ownerId) {
        if (appUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }
        Role ownerRole = roleRepository.findByName("OWNER")
                .orElseGet(() -> roleRepository.save(new Role(null, "OWNER", new java.util.HashSet<>())));
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(ownerRole);
        user.setOwnerId(ownerId);
        return appUserRepository.save(user);
    }

    public Role createRole(String name) {
        return createRole(name, null);
    }

    @Transactional
    public Role createRole(String name, Set<Long> privilegeIds) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("Role already exists");
        }
        Role role = new Role(null, name, new HashSet<>());
        role.setPrivileges(resolvePrivileges(privilegeIds));
        return roleRepository.save(role);
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role updateRole(Long id, String name, Set<Long> privilegeIds) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        if (name != null && !name.isBlank()) {
            roleRepository.findByName(name)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> { throw new IllegalStateException("Role already exists"); });
            role.setName(name);
        }
        if (privilegeIds != null) {
            role.getPrivileges().clear();
            role.getPrivileges().addAll(resolvePrivileges(privilegeIds));
        }
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public Privilege createPrivilege(String name) {
        if (privilegeRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("Privilege already exists");
        }
        return privilegeRepository.save(new Privilege(null, name));
    }

    public List<Privilege> getPrivileges() {
        return privilegeRepository.findAll();
    }

    public Privilege updatePrivilege(Long id, String name) {
        Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Privilege not found"));
        if (name != null && !name.isBlank()) {
            privilegeRepository.findByName(name)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> { throw new IllegalStateException("Privilege already exists"); });
            privilege.setName(name);
        }
        return privilegeRepository.save(privilege);
    }

    public void deletePrivilege(Long id) {
        privilegeRepository.deleteById(id);
    }

    @Transactional
    public Role assignPrivilegeToRole(String roleName, String privilegeName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Privilege privilege = privilegeRepository.findByName(privilegeName)
                .orElseThrow(() -> new IllegalArgumentException("Privilege not found"));
        role.getPrivileges().add(privilege);
        return roleRepository.save(role);
    }

    private Set<Privilege> resolvePrivileges(Set<Long> privilegeIds) {
        if (privilegeIds == null || privilegeIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Privilege> privileges = privilegeRepository.findAllById(privilegeIds);
        if (privileges.size() != privilegeIds.size()) {
            throw new IllegalArgumentException("One or more selected privileges do not exist");
        }
        return new HashSet<>(privileges);
    }

    public AppUser createUser(String username, String password, String roleName, String ownerId) {
        if (appUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setOwnerId(ownerId);
        return appUserRepository.save(user);
    }

    public List<AppUser> getUsers() {
        return appUserRepository.findAll();
    }

    public AppUser updateUser(Long id, String username, String password, String roleName, String ownerId) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (username != null && !username.isBlank() && !username.equals(user.getUsername())) {
            appUserRepository.findByUsername(username)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> { throw new IllegalStateException("Username already exists"); });
            user.setUsername(username);
        }
        if (password != null && !password.isBlank()) user.setPassword(passwordEncoder.encode(password));
        if (roleName != null && !roleName.isBlank()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.setRole(role);
        }
        user.setOwnerId(ownerId);
        return appUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        appUserRepository.deleteById(id);
    }
}
