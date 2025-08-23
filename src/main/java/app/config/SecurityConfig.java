package app.config;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // reemplaza EnableGlobalMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwt;

    public SecurityConfig(JwtTokenProvider jwt) { this.jwt = jwt; }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // === CORS ===
                .cors(cors -> {
                    var cfg = new org.springframework.web.cors.CorsConfiguration();
                    cfg.setAllowedOrigins(java.util.List.of(
                            "http://127.0.0.1:5500",
                            "http://localhost:5500",
                            "http://localhost:3000"
                    ));
                    cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                    // Acepta cualquier header que envíe el browser (incluye Authorization, etc.)
                    cfg.setAllowedHeaders(java.util.List.of("*"));
                    // Por si en algún momento devolvés el header Authorization y querés leerlo del lado cliente
                    cfg.setExposedHeaders(java.util.List.of("Authorization"));
                    cfg.setAllowCredentials(true);

                    var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", cfg);
                    cors.configurationSource(source);
                })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ⭐ IMPORTANTE: permitir el preflight OPTIONS para todas las rutas
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Públicos
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // Publicaciones (lista + detalle)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/publicaciones/**").permitAll()

                        // Vehículos (detalle + recursos públicos del vehículo)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*/documentos").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*/imagenes").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*/eventos").permitAll()

                        // Documentos (detalle)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/documentos/*").permitAll()

                        // Eventos (detalle)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/eventos/*").permitAll()

                        // Verificación concesionaria (form)
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/concesionariaVerif").permitAll()

                        // Privados (requieren token)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuth(),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuth() { return new JwtAuthFilter(secretKey()); }

    @Bean
    public SecretKey secretKey() { return jwt.getSecretKey(); }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

}
