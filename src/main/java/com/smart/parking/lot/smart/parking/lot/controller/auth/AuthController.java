package com.smart.parking.lot.smart.parking.lot.controller.auth;

import com.smart.parking.lot.smart.parking.lot.entity.auth.AppUser;
import com.smart.parking.lot.smart.parking.lot.entity.auth.Role;
import com.smart.parking.lot.smart.parking.lot.entity.auth.Privilege;
import com.smart.parking.lot.smart.parking.lot.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String token = authService.login(body.get("username"), body.get("password"));
        return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/register-owner")
    public ResponseEntity<AppUser> registerOwner(@RequestBody Map<String, String> body) {
        AppUser user = authService.registerOwner(body.get("username"), body.get("password"), body.get("ownerId"));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // Admin endpoints
    @GetMapping("/admin/roles")
    public ResponseEntity<List<Role>> getRoles() {
        return new ResponseEntity<>(authService.getRoles(), HttpStatus.OK);
    }

    @PostMapping("/admin/roles")
    public ResponseEntity<Role> createRole(@RequestBody Map<String, Object> body) {
        Role role = authService.createRole(String.valueOf(body.get("name")));
        Set<Long> privilegeIds = longSet(body.get("privilegeIds"));
        if (privilegeIds != null && role.getId() != null) {
            role = authService.updateRole(role.getId(), role.getName(), privilegeIds);
        }
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @PutMapping("/admin/roles/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return new ResponseEntity<>(authService.updateRole(id, stringValue(body.get("name")), longSet(body.get("privilegeIds"))), HttpStatus.OK);
    }

    @DeleteMapping("/admin/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        authService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/admin/privileges")
    public ResponseEntity<List<Privilege>> getPrivileges() {
        return new ResponseEntity<>(authService.getPrivileges(), HttpStatus.OK);
    }

    @PostMapping("/admin/privileges")
    public ResponseEntity<Privilege> createPrivilege(@RequestBody Map<String, Object> body) {
        return new ResponseEntity<>(authService.createPrivilege(String.valueOf(body.get("name"))), HttpStatus.CREATED);
    }

    @PutMapping("/admin/privileges/{id}")
    public ResponseEntity<Privilege> updatePrivilege(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return new ResponseEntity<>(authService.updatePrivilege(id, stringValue(body.get("name"))), HttpStatus.OK);
    }

    @DeleteMapping("/admin/privileges/{id}")
    public ResponseEntity<Void> deletePrivilege(@PathVariable Long id) {
        authService.deletePrivilege(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/admin/roles/{roleName}/privileges/{privilegeName}")
    public ResponseEntity<Role> assignPrivilege(@PathVariable String roleName, @PathVariable String privilegeName) {
        return new ResponseEntity<>(authService.assignPrivilegeToRole(roleName, privilegeName), HttpStatus.OK);
    }

    @PostMapping("/admin/users")
    public ResponseEntity<AppUser> createUser(@RequestBody Map<String, Object> body) {
        AppUser user = authService.createUser(stringValue(body.get("username")), stringValue(body.get("password")), stringValue(body.get("role")), stringValue(body.get("ownerId")));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return new ResponseEntity<>(authService.getUsers(), HttpStatus.OK);
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        AppUser user = authService.updateUser(id,
                stringValue(body.get("username")),
                stringValue(body.get("password")),
                stringValue(body.get("role")),
                stringValue(body.get("ownerId")));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Set<Long> longSet(Object value) {
        if (!(value instanceof List<?> values)) return null;
        return values.stream()
                .map(item -> Long.valueOf(String.valueOf(item)))
                .collect(Collectors.toSet());
    }
}
