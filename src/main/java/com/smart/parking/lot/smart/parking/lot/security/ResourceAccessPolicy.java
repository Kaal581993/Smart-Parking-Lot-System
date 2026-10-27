package com.smart.parking.lot.smart.parking.lot.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Shared rules for HTTP authorization and the frontend's available actions. */
public final class ResourceAccessPolicy {
    private ResourceAccessPolicy() {}

    private static final Map<String, Map<String, List<String>>> RULES = Map.ofEntries(
            Map.entry("roles", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_ROLES", "ROLE_READ", "MANAGE_ROLES"),
                    "create", List.of("ROLE_ADMIN", "CREATE_ROLES", "ROLE_CREATE", "MANAGE_ROLES"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_ROLES", "ROLE_UPDATE", "MANAGE_ROLES"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_ROLES", "ROLE_DELETE", "MANAGE_ROLES"))),
            Map.entry("privileges", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_PRIVILEGES", "PRIVILEGE_READ", "MANAGE_PRIVILEGES"),
                    "create", List.of("ROLE_ADMIN", "CREATE_PRIVILEGES", "PRIVILEGE_CREATE", "MANAGE_PRIVILEGES"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_PRIVILEGES", "PRIVILEGE_UPDATE", "MANAGE_PRIVILEGES"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_PRIVILEGES", "PRIVILEGE_DELETE", "MANAGE_PRIVILEGES"))),
            Map.entry("users", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_USERS", "USER_READ", "MANAGE_USERS"),
                    "create", List.of("ROLE_ADMIN", "CREATE_USERS", "USER_CREATE", "MANAGE_USERS"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_USERS", "USER_UPDATE", "MANAGE_USERS"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_USERS", "USER_DELETE", "MANAGE_USERS"))),
            Map.entry("addresses", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_ADDRESSES", "READ_ADDRESS", "ADDRESS_READ", "MANAGE_ADDRESSES"),
                    "create", List.of("ROLE_ADMIN", "CREATE_ADDRESSES", "CREATE_ADDRESS", "ADDRESS_CREATE", "MANAGE_ADDRESSES"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_ADDRESSES", "UPDATE_ADDRESS", "ADDRESS_UPDATE", "MANAGE_ADDRESSES"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_ADDRESSES", "DELETE_ADDRESS", "ADDRESS_DELETE", "MANAGE_ADDRESSES"))),
            Map.entry("ownerAadhaar", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_OWNER_AADHAAR", "READ_AADHAAR", "OWNER_AADHAAR_READ", "AADHAAR_READ", "MANAGE_OWNER_AADHAAR"),
                    "create", List.of("ROLE_ADMIN", "CREATE_OWNER_AADHAAR", "CREATE_AADHAAR", "OWNER_AADHAAR_CREATE", "AADHAAR_CREATE", "MANAGE_OWNER_AADHAAR"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_OWNER_AADHAAR", "UPDATE_AADHAAR", "OWNER_AADHAAR_UPDATE", "AADHAAR_UPDATE", "MANAGE_OWNER_AADHAAR"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_OWNER_AADHAAR", "DELETE_AADHAAR", "OWNER_AADHAAR_DELETE", "AADHAAR_DELETE", "MANAGE_OWNER_AADHAAR"))),
            Map.entry("licenses", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_LICENSES", "READ_LICENSE", "LICENSE_READ", "MANAGE_LICENSES"),
                    "create", List.of("ROLE_ADMIN", "CREATE_LICENSES", "CREATE_LICENSE", "LICENSE_CREATE", "MANAGE_LICENSES"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_LICENSES", "UPDATE_LICENSE", "LICENSE_UPDATE", "MANAGE_LICENSES"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_LICENSES", "DELETE_LICENSE", "LICENSE_DELETE", "MANAGE_LICENSES"))),
            Map.entry("vehicleTypes", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_VEHICLE_TYPES", "READ_VEHICLE_TYPE", "VEHICLE_TYPE_READ", "MANAGE_VEHICLE_TYPES"),
                    "create", List.of("ROLE_ADMIN", "CREATE_VEHICLE_TYPES", "CREATE_VEHICLE_TYPE", "VEHICLE_TYPE_CREATE", "MANAGE_VEHICLE_TYPES"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_VEHICLE_TYPES", "UPDATE_VEHICLE_TYPE", "VEHICLE_TYPE_UPDATE", "MANAGE_VEHICLE_TYPES"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_VEHICLE_TYPES", "DELETE_VEHICLE_TYPE", "VEHICLE_TYPE_DELETE", "MANAGE_VEHICLE_TYPES"))),
            Map.entry("owners", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_OWNERS", "READ_VEHICLE_OWNERS", "VEHICLE_OWNER_READ", "MANAGE_OWNERS"),
                    "create", List.of("ROLE_ADMIN", "CREATE_OWNERS", "CREATE_VEHICLE_OWNERS", "VEHICLE_OWNER_CREATE", "MANAGE_OWNERS"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_OWNERS", "UPDATE_VEHICLE_OWNERS", "VEHICLE_OWNER_UPDATE", "MANAGE_OWNERS"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_OWNERS", "DELETE_VEHICLE_OWNERS", "VEHICLE_OWNER_DELETE", "MANAGE_OWNERS"))),
            Map.entry("vehicles", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_VEHICLES", "VEHICLE_READ", "MANAGE_VEHICLES"),
                    "create", List.of("ROLE_ADMIN", "CREATE_VEHICLES", "VEHICLE_CREATE", "MANAGE_VEHICLES"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_VEHICLES", "VEHICLE_UPDATE", "MANAGE_VEHICLES"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_VEHICLES", "VEHICLE_DELETE", "MANAGE_VEHICLES"))),
            Map.entry("parkingSlots", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_PARKING_SLOTS", "READ_SLOTS", "PARKING_SLOT_READ", "MANAGE_SLOTS"),
                    "create", List.of("ROLE_ADMIN", "CREATE_PARKING_SLOTS", "CREATE_SLOTS", "PARKING_SLOT_CREATE", "MANAGE_SLOTS"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_PARKING_SLOTS", "UPDATE_SLOTS", "PARKING_SLOT_UPDATE", "MANAGE_SLOTS"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_PARKING_SLOTS", "DELETE_SLOTS", "PARKING_SLOT_DELETE", "MANAGE_SLOTS"))),
            Map.entry("parkingTickets", Map.of(
                    "read", List.of("ROLE_ADMIN", "READ_PARKING_TICKETS", "PARKING_TICKET_READ", "MANAGE_PARKING_TICKETS"),
                    "create", List.of("ROLE_ADMIN", "CREATE_PARKING_TICKETS", "PARKING_TICKET_CREATE", "MANAGE_PARKING_TICKETS"),
                    "update", List.of("ROLE_ADMIN", "UPDATE_PARKING_TICKETS", "PARKING_TICKET_UPDATE", "MANAGE_PARKING_TICKETS"),
                    "delete", List.of("ROLE_ADMIN", "DELETE_PARKING_TICKETS", "PARKING_TICKET_DELETE", "MANAGE_PARKING_TICKETS")))
    );

    public static String[] authorities(String resource, String action) {
        return RULES.get(resource).get(action).toArray(String[]::new);
    }

    public static Map<String, Map<String, Boolean>> permissions(Collection<? extends GrantedAuthority> authorities) {
        var names = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        Map<String, Map<String, Boolean>> permissions = new LinkedHashMap<>();
        RULES.forEach((resource, actions) -> {
            Map<String, Boolean> allowed = new LinkedHashMap<>();
            actions.forEach((action, required) -> allowed.put(action, required.stream().anyMatch(names::contains)));
            permissions.put(resource, allowed);
        });
        return permissions;
    }
}

