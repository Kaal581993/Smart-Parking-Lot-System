package com.smart.parking.lot.smart.parking.lot.config;

import com.smart.parking.lot.smart.parking.lot.security.AuthTokenFilter;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthTokenFilter authTokenFilter;

    public SecurityConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login", "/auth/register-owner").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/admin/roles").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_ROLES", "MANAGE_ROLES")
                        .requestMatchers(HttpMethod.POST, "/auth/admin/roles/*/privileges/*").hasAnyAuthority(
                                "ROLE_ADMIN", "ASSIGN_ROLE_PRIVILEGES", "MANAGE_ROLES")
                        .requestMatchers(HttpMethod.POST, "/auth/admin/roles").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_ROLES", "MANAGE_ROLES")
                        .requestMatchers(HttpMethod.PUT, "/auth/admin/roles/*").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_ROLES", "MANAGE_ROLES")
                        .requestMatchers(HttpMethod.DELETE, "/auth/admin/roles/*").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_ROLES", "MANAGE_ROLES")
                        .requestMatchers(HttpMethod.GET, "/auth/admin/privileges").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_PRIVILEGES", "MANAGE_PRIVILEGES")
                        .requestMatchers(HttpMethod.POST, "/auth/admin/privileges").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_PRIVILEGES", "MANAGE_PRIVILEGES")
                        .requestMatchers(HttpMethod.PUT, "/auth/admin/privileges/*").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_PRIVILEGES", "MANAGE_PRIVILEGES")
                        .requestMatchers(HttpMethod.DELETE, "/auth/admin/privileges/*").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_PRIVILEGES", "MANAGE_PRIVILEGES")
                        .requestMatchers(HttpMethod.GET, "/auth/admin/users").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_USERS", "MANAGE_USERS")
                        .requestMatchers(HttpMethod.POST, "/auth/admin/users").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_USERS", "MANAGE_USERS")
                        .requestMatchers(HttpMethod.PUT, "/auth/admin/users/*").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_USERS", "MANAGE_USERS")
                        .requestMatchers(HttpMethod.DELETE, "/auth/admin/users/*").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_USERS", "MANAGE_USERS")
                        .requestMatchers("/auth/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/auth/logout").authenticated()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Owner addresses
                        .requestMatchers(HttpMethod.GET, "/address/**").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_ADDRESSES", "READ_ADDRESS", "ADDRESS_READ", "MANAGE_ADDRESSES")
                        .requestMatchers(HttpMethod.POST, "/address/**").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_ADDRESSES", "CREATE_ADDRESS", "ADDRESS_CREATE", "MANAGE_ADDRESSES")
                        .requestMatchers(HttpMethod.PUT, "/address/**").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_ADDRESSES", "UPDATE_ADDRESS", "ADDRESS_UPDATE", "MANAGE_ADDRESSES")
                        .requestMatchers(HttpMethod.DELETE, "/address/**").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_ADDRESSES", "DELETE_ADDRESS", "ADDRESS_DELETE", "MANAGE_ADDRESSES")

                        // Aadhaar and driving license records
                        .requestMatchers(HttpMethod.GET, "/aadhaar/**").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_OWNER_AADHAAR", "READ_AADHAAR", "OWNER_AADHAAR_READ", "AADHAAR_READ", "MANAGE_OWNER_AADHAAR")
                        .requestMatchers(HttpMethod.POST, "/aadhaar/**").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_OWNER_AADHAAR", "CREATE_AADHAAR", "OWNER_AADHAAR_CREATE", "AADHAAR_CREATE", "MANAGE_OWNER_AADHAAR")
                        .requestMatchers(HttpMethod.PUT, "/aadhaar/**").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_OWNER_AADHAAR", "UPDATE_AADHAAR", "OWNER_AADHAAR_UPDATE", "AADHAAR_UPDATE", "MANAGE_OWNER_AADHAAR")
                        .requestMatchers(HttpMethod.DELETE, "/aadhaar/**").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_OWNER_AADHAAR", "DELETE_AADHAAR", "OWNER_AADHAAR_DELETE", "AADHAAR_DELETE", "MANAGE_OWNER_AADHAAR")
                        .requestMatchers(HttpMethod.GET, "/driving_license/**").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_LICENSES", "READ_LICENSE", "LICENSE_READ", "MANAGE_LICENSES")
                        .requestMatchers(HttpMethod.POST, "/driving_license/**").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_LICENSES", "CREATE_LICENSE", "LICENSE_CREATE", "MANAGE_LICENSES")
                        .requestMatchers(HttpMethod.PUT, "/driving_license/**").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_LICENSES", "UPDATE_LICENSE", "LICENSE_UPDATE", "MANAGE_LICENSES")
                        .requestMatchers(HttpMethod.DELETE, "/driving_license/**").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_LICENSES", "DELETE_LICENSE", "LICENSE_DELETE", "MANAGE_LICENSES")

                        // Vehicle types
                        .requestMatchers(HttpMethod.GET, "/vehicle_type/**").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_VEHICLE_TYPES", "READ_VEHICLE_TYPE", "VEHICLE_TYPE_READ", "MANAGE_VEHICLE_TYPES")
                        .requestMatchers(HttpMethod.POST, "/vehicle_type/**").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_VEHICLE_TYPES", "CREATE_VEHICLE_TYPE", "VEHICLE_TYPE_CREATE", "MANAGE_VEHICLE_TYPES")
                        .requestMatchers(HttpMethod.PUT, "/vehicle_type/**").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_VEHICLE_TYPES", "UPDATE_VEHICLE_TYPE", "VEHICLE_TYPE_UPDATE", "MANAGE_VEHICLE_TYPES")
                        .requestMatchers(HttpMethod.DELETE, "/vehicle_type/**").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_VEHICLE_TYPES", "DELETE_VEHICLE_TYPE", "VEHICLE_TYPE_DELETE", "MANAGE_VEHICLE_TYPES")

                        // Vehicle owners and nested owner vehicle routes
                        .requestMatchers(HttpMethod.GET, "/vehicle_owners/*/vehicles").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_OWNER_VEHICLES", "READ_VEHICLES", "READ_OWNERS", "READ_VEHICLE_OWNERS", "MANAGE_OWNERS")
                        .requestMatchers(HttpMethod.POST, "/vehicle_owners/*/vehicles").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_OWNER_VEHICLES", "CREATE_VEHICLES", "CREATE_OWNERS", "MANAGE_OWNERS", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.GET, "/vehicle_owners/**").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_OWNERS", "READ_VEHICLE_OWNERS", "VEHICLE_OWNER_READ", "MANAGE_OWNERS")
                        .requestMatchers(HttpMethod.POST, "/vehicle_owners/**").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_OWNERS", "CREATE_VEHICLE_OWNERS", "VEHICLE_OWNER_CREATE", "MANAGE_OWNERS")
                        .requestMatchers(HttpMethod.PUT, "/vehicle_owners/**").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_OWNERS", "UPDATE_VEHICLE_OWNERS", "VEHICLE_OWNER_UPDATE", "MANAGE_OWNERS")
                        .requestMatchers(HttpMethod.DELETE, "/vehicle_owners/**").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_OWNERS", "DELETE_VEHICLE_OWNERS", "VEHICLE_OWNER_DELETE", "MANAGE_OWNERS")

                        // Vehicle details and vehicle-specific routes
                        .requestMatchers(HttpMethod.GET, "/vehicles/*/active-ticket").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_ACTIVE_VEHICLE_TICKETS", "READ_PARKING_TICKETS", "READ_VEHICLES", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.POST, "/vehicles/batch").hasAnyAuthority(
                                "ROLE_ADMIN", "BATCH_CREATE_VEHICLES", "CREATE_VEHICLES", "VEHICLE_CREATE", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.GET, "/vehicles/**").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_VEHICLES", "VEHICLE_READ", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.POST, "/vehicles/**").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_VEHICLES", "VEHICLE_CREATE", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.PUT, "/vehicles/**").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_VEHICLES", "VEHICLE_UPDATE", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.DELETE, "/vehicles/**").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_VEHICLES", "VEHICLE_DELETE", "MANAGE_VEHICLES")

                        // Parking slot routes and slot-specific operations
                        .requestMatchers(HttpMethod.GET, "/parking_slots/*/tickets").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_SLOT_TICKETS", "READ_PARKING_TICKETS", "READ_PARKING_SLOTS", "READ_SLOTS", "MANAGE_SLOTS")
                        .requestMatchers(HttpMethod.GET, "/parking_slots/scan-overstay").hasAnyAuthority(
                                "ROLE_ADMIN", "SCAN_OVERSTAY", "READ_PARKING_TICKETS", "READ_PARKING_SLOTS", "READ_SLOTS", "MANAGE_SLOTS")
                        .requestMatchers(HttpMethod.GET, "/parking_slots/**").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_PARKING_SLOTS", "READ_SLOTS", "PARKING_SLOT_READ", "MANAGE_SLOTS")
                        .requestMatchers(HttpMethod.POST, "/parking_slots/**").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_PARKING_SLOTS", "CREATE_SLOTS", "PARKING_SLOT_CREATE", "MANAGE_SLOTS")
                        .requestMatchers(HttpMethod.PUT, "/parking_slots/**").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_PARKING_SLOTS", "UPDATE_SLOTS", "PARKING_SLOT_UPDATE", "MANAGE_SLOTS")
                        .requestMatchers(HttpMethod.DELETE, "/parking_slots/**").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_PARKING_SLOTS", "DELETE_SLOTS", "PARKING_SLOT_DELETE", "MANAGE_SLOTS")

                        // Parking ticket CRUD and workflow operations
                        .requestMatchers(HttpMethod.POST, "/parking_tickets/check-in").hasAnyAuthority(
                                "ROLE_ADMIN", "CHECK_IN_TICKETS", "CREATE_PARKING_TICKETS", "MANAGE_PARKING_TICKETS")
                        .requestMatchers(HttpMethod.POST, "/parking_tickets/batch-check-in").hasAnyAuthority(
                                "ROLE_ADMIN", "BATCH_CHECK_IN_TICKETS", "CHECK_IN_TICKETS", "MANAGE_PARKING_TICKETS")
                        .requestMatchers(HttpMethod.POST, "/parking_tickets/undo-last-checkin").hasAnyAuthority(
                                "ROLE_ADMIN", "UNDO_LAST_CHECKIN", "MANAGE_PARKING_TICKETS")
                        .requestMatchers(HttpMethod.POST, "/parking_tickets/*/check-out").hasAnyAuthority(
                                "ROLE_ADMIN", "CHECK_OUT_TICKETS", "UPDATE_PARKING_TICKETS", "MANAGE_PARKING_TICKETS")
                        .requestMatchers(HttpMethod.POST, "/parking_tickets/*/pay").hasAnyAuthority(
                                "ROLE_ADMIN", "PAY_TICKETS", "UPDATE_PARKING_TICKETS", "MANAGE_PARKING_TICKETS")
                        .requestMatchers(HttpMethod.GET, "/parking_tickets/**").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_PARKING_TICKETS", "PARKING_TICKET_READ", "MANAGE_PARKING_TICKETS")
                        .requestMatchers(HttpMethod.POST, "/parking_tickets/**").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_PARKING_TICKETS", "PARKING_TICKET_CREATE", "MANAGE_PARKING_TICKETS")
                        .requestMatchers(HttpMethod.PUT, "/parking_tickets/**").hasAnyAuthority(
                                "ROLE_ADMIN", "UPDATE_PARKING_TICKETS", "PARKING_TICKET_UPDATE", "MANAGE_PARKING_TICKETS")
                        .requestMatchers(HttpMethod.DELETE, "/parking_tickets/**").hasAnyAuthority(
                                "ROLE_ADMIN", "DELETE_PARKING_TICKETS", "PARKING_TICKET_DELETE", "MANAGE_PARKING_TICKETS")

                        .requestMatchers("/actuator/health").hasAnyAuthority("ROLE_ADMIN", "READ_ACTUATOR_HEALTH")
                        .requestMatchers("/actuator/metrics/**").hasAnyAuthority("ROLE_ADMIN", "READ_ACTUATOR_METRICS")
                        .requestMatchers("/actuator/info").hasAnyAuthority("ROLE_ADMIN", "READ_ACTUATOR_INFO")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
