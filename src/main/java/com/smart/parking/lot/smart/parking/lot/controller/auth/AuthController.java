package com.smart.parking.lot.smart.parking.lot.controller.auth;

import com.smart.parking.lot.smart.parking.lot.entity.auth.AppUser;
import com.smart.parking.lot.smart.parking.lot.entity.auth.Role;
import com.smart.parking.lot.smart.parking.lot.entity.auth.Privilege;
import com.smart.parking.lot.smart.parking.lot.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @PostMapping("/admin/roles")
    public ResponseEntity<Role> createRole(@RequestBody Map<String, String> body) {
        return new ResponseEntity<>(authService.createRole(body.get("name")), HttpStatus.CREATED);
    }

    @PostMapping("/admin/privileges")
    public ResponseEntity<Privilege> createPrivilege(@RequestBody Map<String, String> body) {
        return new ResponseEntity<>(authService.createPrivilege(body.get("name")), HttpStatus.CREATED);
    }

    @PostMapping("/admin/roles/{roleName}/privileges/{privilegeName}")
    public ResponseEntity<Role> assignPrivilege(@PathVariable String roleName, @PathVariable String privilegeName) {
        return new ResponseEntity<>(authService.assignPrivilegeToRole(roleName, privilegeName), HttpStatus.OK);
    }

    @PostMapping("/admin/users")
    public ResponseEntity<AppUser> createUser(@RequestBody Map<String, String> body) {
        AppUser user = authService.createUser(body.get("username"), body.get("password"), body.get("role"), body.get("ownerId"));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
