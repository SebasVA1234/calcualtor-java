package com.ecualand.calculator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${BASIC_USER:admin}")
  private String basicUser;

  @Value("${BASIC_PASS:admin}")
  private String basicPass;

  // Orígenes permitidos para CORS (para Railway pon aquí tu dominio frontend)
  @Value("${APP_ALLOWED_ORIGINS:http://localhost:8080}")
  private String allowedOrigins;

  // Usuario en memoria para Basic Auth
  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails u = User.withUsername(basicUser)
        .password(encoder.encode(basicPass))
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(u);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/", "/index.html", "/static/**", "/assets/**").permitAll()
          .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
          .requestMatchers("/api/**").authenticated()   // API protegida
          .anyRequest().permitAll()
      )
      .httpBasic(Customizer.withDefaults())             // Habilita Basic Auth
      .headers(h -> {
        // Cada cabecera va por separado
        h.frameOptions(fo -> fo.sameOrigin());
        // Ajusta la directiva connect-src para desarrollo (agrega tu puerto si usas otro)
        h.contentSecurityPolicy(csp -> csp.policyDirectives(
            "default-src 'self'; " +
            "img-src 'self' data: https:; " +
            "script-src 'self' https://cdn.tailwindcss.com; " +
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
            "font-src 'self' https://fonts.gstatic.com; " +
            // Permite llamadas fetch/XHR al origen del frontend
            "connect-src 'self' " + allowedOrigins + ";"
        ));
        h.referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER));
        h.permissionsPolicy(p -> p.policy("geolocation=(), microphone=(), camera=()"));
      });

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    // Dividimos la lista de orígenes permitidos por comas por si hay más de uno
    cfg.setAllowedOrigins(List.of(allowedOrigins.split(",")));
    cfg.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
    // Permitimos Content-Type y Authorization para enviar credenciales básicas
    cfg.setAllowedHeaders(List.of("Content-Type", "Authorization"));
    // Habilitamos credenciales para que se envíe la cabecera Authorization
    cfg.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
