package com.jacpower.groupsApp.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private JwtAuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers( "/auth/authenticate","/user/create", "/role/get/**", "/user/get/**" ).permitAll();
                    auth.requestMatchers("/group/create", "/group/id/**", "/group/get/**", "/member/create",
                            "/member/get", "/member/update/**", "/member/deactivate/**", "/member/activate/**",
                            "/meeting/create", "/meeting/get/**", "/meeting/close/**", "/attendance/get/**",
                            "/attendance/update/**", "/inventory/get/**", "/account/create", "/account/deposit", "/account/withdraw",
                            "/loan/approve/**", "/loan/deny/**", "/lottery/get/**", "/user/update/details","/loan/get/**").hasRole("USER");
                    auth.requestMatchers("/attendance/add", "/group/get").hasAnyRole("USER", "MEMBER");
                    //auth.requestMatchers("/group/get").hasRole("ADMIN");
                    auth.anyRequest().authenticated(); // All other requests need authentication
                })
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
