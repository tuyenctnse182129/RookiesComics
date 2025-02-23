package application.aicomic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
        private final ClientRegistrationRepository clientRegistrationRepository;

        public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
                this.clientRegistrationRepository = clientRegistrationRepository;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                        .csrf(csrf -> csrf.disable());

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Đảm bảo trùng với FE
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
                configuration.setAllowCredentials(true);
                configuration.addExposedHeader("Authorization");
                configuration.addExposedHeader("Cross-Origin-Opener-Policy");
                configuration.addExposedHeader("Cross-Origin-Embedder-Policy");

                // Thêm header này để tránh lỗi COOP
                configuration.addExposedHeader("Cross-Origin-Resource-Policy");

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        /**
         * Xử lý logout thành công cho OIDC
         */
        private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
                OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(
                                clientRegistrationRepository);
                handler.setPostLogoutRedirectUri("http://localhost:8080/users/login"); // URL sau khi logout
                return handler;
        }
}
