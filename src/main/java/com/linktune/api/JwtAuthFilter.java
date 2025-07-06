package com.linktune.api;

import com.linktune.api.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        System.out.println("--- JwtAuthFilter Aktif Oldu ---"); // LOG 1

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("LOG: Authorization header eksik veya 'Bearer ' ile başlamıyor."); // LOG 2
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("LOG: Token header'dan alındı: " + jwt.substring(0, 10) + "..."); // LOG 3

        try {
            username = jwtService.extractUsername(jwt);
            System.out.println("LOG: Token'dan kullanıcı adı çıkarıldı: " + username); // LOG 4

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("LOG: Kullanıcı adı geçerli ve daha önce doğrulanmamış. UserDetails aranıyor..."); // LOG 5
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                System.out.println("LOG: Token geçerliliği kontrol ediliyor..."); // LOG 6
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    System.out.println("LOG: Token GEÇERLİ. Kullanıcı doğrulanıyor..."); // LOG 7
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("LOG: Token GEÇERSİZ!"); // LOG 8
                }
            }
        } catch (Exception e) {
            System.out.println("LOG: Token işlenirken bir HATA oluştu: " + e.getMessage()); // LOG 9
        }

        filterChain.doFilter(request, response);
    }
}