package com.smart.parking.lot.smart.parking.lot.config;

import com.smart.parking.lot.smart.parking.lot.security.AuthTokenFilter;
import com.smart.parking.lot.smart.parking.lot.security.ResourceAccessPolicy;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private List<String> allowedOrigins;

    public SecurityConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
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
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login", "/auth/register-owner").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/admin/roles").hasAnyAuthority(ResourceAccessPolicy.authorities("roles", "read"))
                        .requestMatchers(HttpMethod.POST, "/auth/admin/roles/*/privileges/*").hasAnyAuthority(
                                "ROLE_ADMIN", "ASSIGN_ROLE_PRIVILEGES", "PRIVILEGE_ASSIGN", "MANAGE_ROLES")
                        .requestMatchers(HttpMethod.POST, "/auth/admin/roles").hasAnyAuthority(ResourceAccessPolicy.authorities("roles", "create"))
                        .requestMatchers(HttpMethod.PUT, "/auth/admin/roles/*").hasAnyAuthority(ResourceAccessPolicy.authorities("roles", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/auth/admin/roles/*").hasAnyAuthority(ResourceAccessPolicy.authorities("roles", "delete"))
                        .requestMatchers(HttpMethod.GET, "/auth/admin/privileges").hasAnyAuthority(ResourceAccessPolicy.authorities("privileges", "read"))
                        .requestMatchers(HttpMethod.POST, "/auth/admin/privileges").hasAnyAuthority(ResourceAccessPolicy.authorities("privileges", "create"))
                        .requestMatchers(HttpMethod.PUT, "/auth/admin/privileges/*").hasAnyAuthority(ResourceAccessPolicy.authorities("privileges", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/auth/admin/privileges/*").hasAnyAuthority(ResourceAccessPolicy.authorities("privileges", "delete"))
                        .requestMatchers(HttpMethod.GET, "/auth/admin/users").hasAnyAuthority(ResourceAccessPolicy.authorities("users", "read"))
                        .requestMatchers(HttpMethod.POST, "/auth/admin/users").hasAnyAuthority(ResourceAccessPolicy.authorities("users", "create"))
                        .requestMatchers(HttpMethod.PUT, "/auth/admin/users/*").hasAnyAuthority(ResourceAccessPolicy.authorities("users", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/auth/admin/users/*").hasAnyAuthority(ResourceAccessPolicy.authorities("users", "delete"))
                        .requestMatchers("/auth/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/auth/logout").authenticated()
                        .requestMatchers("/auth/profile", "/auth/profile/**").authenticated()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Owner addresses
                        .requestMatchers(HttpMethod.GET, "/address/**").hasAnyAuthority(ResourceAccessPolicy.authorities("addresses", "read"))
                        .requestMatchers(HttpMethod.POST, "/address/**").hasAnyAuthority(ResourceAccessPolicy.authorities("addresses", "create"))
                        .requestMatchers(HttpMethod.PUT, "/address/**").hasAnyAuthority(ResourceAccessPolicy.authorities("addresses", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/address/**").hasAnyAuthority(ResourceAccessPolicy.authorities("addresses", "delete"))

                        // Aadhaar and driving license records
                        .requestMatchers(HttpMethod.GET, "/aadhaar/**").hasAnyAuthority(ResourceAccessPolicy.authorities("ownerAadhaar", "read"))
                        .requestMatchers(HttpMethod.POST, "/aadhaar/**").hasAnyAuthority(ResourceAccessPolicy.authorities("ownerAadhaar", "create"))
                        .requestMatchers(HttpMethod.PUT, "/aadhaar/**").hasAnyAuthority(ResourceAccessPolicy.authorities("ownerAadhaar", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/aadhaar/**").hasAnyAuthority(ResourceAccessPolicy.authorities("ownerAadhaar", "delete"))
                        .requestMatchers(HttpMethod.GET, "/driving_license/**").hasAnyAuthority(ResourceAccessPolicy.authorities("licenses", "read"))
                        .requestMatchers(HttpMethod.POST, "/driving_license/**").hasAnyAuthority(ResourceAccessPolicy.authorities("licenses", "create"))
                        .requestMatchers(HttpMethod.PUT, "/driving_license/**").hasAnyAuthority(ResourceAccessPolicy.authorities("licenses", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/driving_license/**").hasAnyAuthority(ResourceAccessPolicy.authorities("licenses", "delete"))

                        // Vehicle types
                        .requestMatchers(HttpMethod.GET, "/vehicle_type/**").hasAnyAuthority(ResourceAccessPolicy.authorities("vehicleTypes", "read"))
                        .requestMatchers(HttpMethod.POST, "/vehicle_type/**").hasAnyAuthority(ResourceAccessPolicy.authorities("vehicleTypes", "create"))
                        .requestMatchers(HttpMethod.PUT, "/vehicle_type/**").hasAnyAuthority(ResourceAccessPolicy.authorities("vehicleTypes", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/vehicle_type/**").hasAnyAuthority(ResourceAccessPolicy.authorities("vehicleTypes", "delete"))

                        // Vehicle owners and nested owner vehicle routes
                        .requestMatchers(HttpMethod.GET, "/vehicle_owners/*/vehicles").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_OWNER_VEHICLES", "READ_VEHICLES", "READ_OWNERS", "READ_VEHICLE_OWNERS", "MANAGE_OWNERS")
                        .requestMatchers(HttpMethod.POST, "/vehicle_owners/*/vehicles").hasAnyAuthority(
                                "ROLE_ADMIN", "CREATE_OWNER_VEHICLES", "CREATE_VEHICLES", "CREATE_OWNERS", "MANAGE_OWNERS", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.GET, "/vehicle_owners/**").hasAnyAuthority(ResourceAccessPolicy.authorities("owners", "read"))
                        .requestMatchers(HttpMethod.POST, "/vehicle_owners/**").hasAnyAuthority(ResourceAccessPolicy.authorities("owners", "create"))
                        .requestMatchers(HttpMethod.PUT, "/vehicle_owners/**").hasAnyAuthority(ResourceAccessPolicy.authorities("owners", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/vehicle_owners/**").hasAnyAuthority(ResourceAccessPolicy.authorities("owners", "delete"))

                        // Vehicle details and vehicle-specific routes
                        .requestMatchers(HttpMethod.GET, "/vehicles/*/active-ticket").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_ACTIVE_VEHICLE_TICKETS", "READ_PARKING_TICKETS", "READ_VEHICLES", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.POST, "/vehicles/batch").hasAnyAuthority(
                                "ROLE_ADMIN", "BATCH_CREATE_VEHICLES", "CREATE_VEHICLES", "VEHICLE_CREATE", "MANAGE_VEHICLES")
                        .requestMatchers(HttpMethod.GET, "/vehicles/**").hasAnyAuthority(ResourceAccessPolicy.authorities("vehicles", "read"))
                        .requestMatchers(HttpMethod.POST, "/vehicles/**").hasAnyAuthority(ResourceAccessPolicy.authorities("vehicles", "create"))
                        .requestMatchers(HttpMethod.PUT, "/vehicles/**").hasAnyAuthority(ResourceAccessPolicy.authorities("vehicles", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/vehicles/**").hasAnyAuthority(ResourceAccessPolicy.authorities("vehicles", "delete"))

                        // Parking slot routes and slot-specific operations
                        .requestMatchers(HttpMethod.GET, "/parking_slots/*/tickets").hasAnyAuthority(
                                "ROLE_ADMIN", "READ_SLOT_TICKETS", "READ_PARKING_TICKETS", "READ_PARKING_SLOTS", "READ_SLOTS", "MANAGE_SLOTS")
                        .requestMatchers(HttpMethod.GET, "/parking_slots/scan-overstay").hasAnyAuthority(
                                "ROLE_ADMIN", "SCAN_OVERSTAY", "READ_PARKING_TICKETS", "READ_PARKING_SLOTS", "READ_SLOTS", "MANAGE_SLOTS")
                        .requestMatchers(HttpMethod.GET, "/parking_slots/**").hasAnyAuthority(ResourceAccessPolicy.authorities("parkingSlots", "read"))
                        .requestMatchers(HttpMethod.POST, "/parking_slots/**").hasAnyAuthority(ResourceAccessPolicy.authorities("parkingSlots", "create"))
                        .requestMatchers(HttpMethod.PUT, "/parking_slots/**").hasAnyAuthority(ResourceAccessPolicy.authorities("parkingSlots", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/parking_slots/**").hasAnyAuthority(ResourceAccessPolicy.authorities("parkingSlots", "delete"))

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
                        .requestMatchers(HttpMethod.GET, "/parking_tickets/**").hasAnyAuthority(ResourceAccessPolicy.authorities("parkingTickets", "read"))
                        .requestMatchers(HttpMethod.POST, "/parking_tickets/**").hasAnyAuthority(ResourceAccessPolicy.authorities("parkingTickets", "create"))
                        .requestMatchers(HttpMethod.PUT, "/parking_tickets/**").hasAnyAuthority(ResourceAccessPolicy.authorities("parkingTickets", "update"))
                        .requestMatchers(HttpMethod.DELETE, "/parking_tickets/**").hasAnyAuthority(ResourceAccessPolicy.authorities("parkingTickets", "delete"))

                        .requestMatchers("/actuator/health").hasAnyAuthority("ROLE_ADMIN", "READ_ACTUATOR_HEALTH")
                        .requestMatchers("/actuator/metrics/**").hasAnyAuthority("ROLE_ADMIN", "READ_ACTUATOR_METRICS")
                        .requestMatchers("/actuator/info").hasAnyAuthority("ROLE_ADMIN", "READ_ACTUATOR_INFO")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Sign in again: the session token is missing or invalid.\"}");
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"You do not have the privilege required for this action.\"}");
                        }))
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
