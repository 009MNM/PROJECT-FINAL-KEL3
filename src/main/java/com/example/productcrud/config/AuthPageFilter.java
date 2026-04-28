package com.example.productcrud.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class AuthPageFilter extends OncePerRequestFilter {

    // Halaman-halaman yang tidak boleh diakses jika sudah login
    private static final Set<String> AUTH_PAGES = Set.of(
            "/login",
            "/register",
            "/forgot-password",
            "/reset-password"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Cek apakah user sedang mengakses halaman auth
        if (AUTH_PAGES.contains(requestURI)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Logika pengecekan status autentikasi
            if (authentication != null
                    && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken)) {

                // Arahkan ke /home agar sinkron dengan SecurityConfig kamu
                response.sendRedirect("/home");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}