package com.smart.parking.lot.smart.parking.lot.config;


import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Allow anyone to access the H2 console path
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(

                                "/address/add_address",
                                "/address/get_address/{addressId}",
                                "/address/get_all_address",
                                "/address/delete_address/{addressId}").permitAll()
                        .requestMatchers("/aadhaar/add_aadhaar",
                                "/aadhaar/get_owner_aadhaar/{ownerAadhaarId}",
                                "/aadhaar/get_all_aadhaar",
                                "/aadhaar/delete_aadhaar/{ownerAadhaarId}").permitAll()
                        .requestMatchers("/license/add_license",
                                "/license/get_license/{licenseId}",
                                "/license/get_all_license",
                                "/license/delete_license/{licenseId}").permitAll()
                        .anyRequest().authenticated()
                )
                // Disable CSRF protection specifically for the H2 console
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathRequest.toH2Console())
                        .ignoringRequestMatchers("/address/add_address")
                        .ignoringRequestMatchers("/address/delete_address/{addressId}")
                        .ignoringRequestMatchers("/aadhaar/add_aadhaar")
                        .ignoringRequestMatchers("/aadhaar/delete_aadhaar/{ownerAadhaarId}")
                        .ignoringRequestMatchers("/license/add_license")
                        .ignoringRequestMatchers("/license/delete_license/{licenseId}"))
                // Allow H2 console to run inside HTML frames
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
