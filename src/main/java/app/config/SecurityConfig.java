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
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwt;

    public SecurityConfig(JwtTokenProvider jwt) { this.jwt = jwt; }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {
                    var cfg = new org.springframework.web.cors.CorsConfiguration();
                    cfg.setAllowedOrigins(java.util.List.of(
                            "http://127.0.0.1:5500",
                            "http://localhost:5500",
                            "http://localhost:3000"
                    ));
                    cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));

                    cfg.setAllowedHeaders(java.util.List.of("*"));

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

                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Usuarios Públicos
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/usuarios/publico/*").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/usuarios/*/publicaciones/count").permitAll()

                        //Usuarios
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/usuarios/*/oferta/toggle")
                        .hasAnyAuthority("ROL_USER","ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN")

                        // Publicaciones
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/publicaciones/**").permitAll()

                        // Vehículos
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*/documentos").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*/imagenes").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*/eventos").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/vehiculos/*/eventos/eliminados").permitAll()

                        // Documentos
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/documentos/**").permitAll()

                        // Eventos
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/eventos/*").permitAll()

                        // Verificación concesionaria y taller
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/concesionariaTallerVerif/**").permitAll()

                        // Validaciones
                        .requestMatchers(org.springframework.http.HttpMethod.POST,
                                "/usuarios/validacion/frente",
                                "/usuarios/validacion/dorso",
                                "/usuarios/validacion/enviar")
                        .hasAnyAuthority("ROL_USER","ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET,
                                "/usuarios/validacion/frente-url",
                                "/usuarios/validacion/dorso-url")
                        .hasAnyAuthority("ROL_USER","ROL_ADMIN")

                        // Admin validar / rechazar particulares
                        .requestMatchers(org.springframework.http.HttpMethod.GET,
                                "/usuarios/validacion/*/dni")
                        .hasAuthority("ROL_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST,
                                "/usuarios/validacion/*/validar",
                                "/usuarios/validacion/*/rechazar")
                        .hasAuthority("ROL_ADMIN")

                        .requestMatchers(org.springframework.http.HttpMethod.POST,
                                "/usuarios/validacion/archivo/*",
                                "/usuarios/validacion/enviarValidacion/*")
                        .hasAnyAuthority("ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN","ROL_USER")

                        // Obtener URL del archivo
                        .requestMatchers(org.springframework.http.HttpMethod.GET,
                                "/usuarios/validacion/*/archivo")
                        .hasAnyAuthority("ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN","ROL_USER")

                        // Admin validar / rechazar particulares
                        .requestMatchers(org.springframework.http.HttpMethod.POST,
                                "/usuarios/validacion/*/validarTallerConcesionaria",
                                "/usuarios/validacion/*/rechazarTallerConcesionaria")
                        .hasAuthority("ROL_ADMIN")

                        // fotos de perfil de usuarios
                        .requestMatchers(HttpMethod.GET, "/usuarios/*/fotoPerfil").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/*/fotoPerfil").hasAnyAuthority("ROL_USER","ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/*/fotoPerfil").hasAnyAuthority("ROL_USER","ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN")

                        //Favoritos
                        .requestMatchers(org.springframework.http.HttpMethod.GET,
                                "/usuarios/*/favoritos/**")
                        .hasAnyAuthority("ROL_USER","ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST,
                                "/usuarios/*/favoritos/**")
                        .hasAnyAuthority("ROL_USER","ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE,
                                "/usuarios/*/favoritos/**")
                        .hasAnyAuthority("ROL_USER","ROL_TALLER","ROL_CONCESIONARIO","ROL_ADMIN")


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
