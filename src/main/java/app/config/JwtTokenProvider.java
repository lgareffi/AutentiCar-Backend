package app.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import app.model.entity.Usuarios;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretBase64;

    @Value("${jwt.exp-minutes:120}")
    private long expMinutes;

    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Mapea tus roles de entidad a las autoridades que usa @PreAuthorize
    private String mapAuthority(Usuarios u) {
        if (u.getRol() == Usuarios.Rol.ADMIN) return "ROL_ADMIN";
        if (u.getRol() == Usuarios.Rol.TALLER) return "ROL_TALLER";
        // PARTICULAR y CONCESIONARIO comparten permisos => ROL_USER
        return "ROL_USER";
    }

    public String generarToken(Usuarios u) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(u.getIdUsuario()))
                .claim("mail", u.getMail())
                .claim("rol", mapAuthority(u))         // <- lo lee tu JwtAuthFilter
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
