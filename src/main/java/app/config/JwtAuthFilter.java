package app.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Filtro JWT:
 * - Lee el header Authorization: Bearer <token>
 * - Valida firma y expiración con la misma SecretKey que usa JwtTokenProvider
 * - Extrae subject (username) y claim "rol" -> GrantedAuthority("ROL_*")
 * - Pone la Authentication en el SecurityContext para que funcionen @PreAuthorize(...)
 */
public class JwtAuthFilter extends OncePerRequestFilter{
    private final SecretKey secretKey;

    public JwtAuthFilter(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Permite preflight CORS sin validar token
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            String bearer = request.getHeader("Authorization");
            if (bearer == null || !bearer.startsWith("Bearer ")) {
                // No hay token -> seguir (para endpoints permitAll)
                filterChain.doFilter(request, response);
                return;
            }

            String token = bearer.substring(7); // saca "Bearer "
            // Valida firma + expiración (lanzará excepción si es inválido/expirado)
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = jws.getBody();
            String username = claims.getSubject();            // lo que pusiste en setSubject (mail)
            String rol = claims.get("rol", String.class);     // p.ej. "ROL_ADMIN", "ROL_TALLER", "ROL_USER"

            if (username != null && rol != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(rol));

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException e) {
            // Token presente pero inválido/expirado -> 401
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
        }
    }


}
