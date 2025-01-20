package com.lordbyron.backendByronLibrary.Security;

import com.lordbyron.backendByronLibrary.entity.Users;
import com.lordbyron.backendByronLibrary.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (jwtTokenUtil.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Token expirado\"}");
                return; // Finaliza la ejecución del filtro
            }

            String email = jwtTokenUtil.getUsernameFromToken(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Users user = usersRepository.findByEmail(email).orElse(null);

                if (user != null && jwtTokenUtil.validateToken(token)) {
                    Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .toList();

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}