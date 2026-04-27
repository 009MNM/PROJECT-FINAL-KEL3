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

    // TAMBAHKAN JALUR BARU DI SINI, LEK!
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

        if (AUTH_PAGES.contains(requestURI)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null
                    && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken)) {

                // Kalau user maksa buka halaman forgot padahal sudah login,
                // lempar ke halaman products
                response.sendRedirect("/products");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}