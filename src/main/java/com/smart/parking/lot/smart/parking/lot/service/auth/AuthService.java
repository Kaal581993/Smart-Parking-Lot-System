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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
        if (roleRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("Role already exists");
        }
        return roleRepository.save(new Role(null, name, new java.util.HashSet<>()));
    }

    public Privilege createPrivilege(String name) {
        if (privilegeRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("Privilege already exists");
        }
        return privilegeRepository.save(new Privilege(null, name));
    }

    public Role assignPrivilegeToRole(String roleName, String privilegeName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        Privilege privilege = privilegeRepository.findByName(privilegeName)
                .orElseThrow(() -> new IllegalArgumentException("Privilege not found"));
        role.getPrivileges().add(privilege);
        return roleRepository.save(role);
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
}
