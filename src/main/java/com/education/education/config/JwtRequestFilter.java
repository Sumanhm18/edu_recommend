package com.education.education.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        boolean isGuest = false;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                isGuest = jwtTokenUtil.isGuestToken(jwtToken);
            } catch (Exception e) {
                logger.error("Unable to get JWT Token or token is expired");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            boolean isValidToken = false;

            if (isGuest) {
                isValidToken = jwtTokenUtil.validateTokenForGuest(jwtToken, username);
            } else {
                // For now, we'll create a simple validation since we don't have
                // UserDetailsService
                // In production, you should implement proper UserDetailsService
                try {
                    if (!jwtTokenUtil.getUsernameFromToken(jwtToken).isEmpty()) {
                        isValidToken = true;
                    }
                } catch (Exception e) {
                    isValidToken = false;
                }
            }

            if (isValidToken) {
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(isGuest ? "ROLE_GUEST" : "ROLE_USER"));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }
}