package com.example.productcrud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthPageFilter authPageFilter;

    @Lazy
    public SecurityConfig(AuthPageFilter authPageFilter) {
        this.authPageFilter = authPageFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. MATIKAN CSRF (Sangat penting agar POST/DELETE data tidak diblokir)
                .csrf(csrf -> csrf.disable())

                // 2. DAFTARKAN FILTER (Letakkan di awal sebelum routing)
                .addFilterBefore(authPageFilter, UsernamePasswordAuthenticationFilter.class)

                // 3. PENGATURAN AKSES URL
                .authorizeHttpRequests(auth -> auth
                        // URL yang bisa diakses tanpa login
                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password", "/error", "/css/**", "/js/**").permitAll()
                        // URL Category & Produk butuh login (anyRequest)
                        .anyRequest().authenticated()
                )

                // 4. KONFIGURASI LOGIN
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )

                // 5. KONFIGURASI LOGOUT
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}