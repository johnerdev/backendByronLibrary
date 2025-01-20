package com.lordbyron.backendByronLibrary.Security;
import com.lordbyron.backendByronLibrary.entity.Role;
import com.lordbyron.backendByronLibrary.entity.Users;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

    private final SecretKey secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public JwtTokenUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(Role::getName).toList());
        claims.put("name", user.getName()); // Agregar el nombre del usuario como una reclamación

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail()) // Usar el email como el sujeto principal
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expirationDate.before(new Date());
    }

    public Map<String, Object> getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
