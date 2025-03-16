package application.aicomic.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import application.aicomic.enums.Role;

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
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/chapters/**").hasAnyAuthority(
                                        "ROLE_" + Role.CUSTOMER_AUTHOR.name(),
                                        "ROLE_" + Role.CUSTOMER_VIP.name(),
                                        "ROLE_" + Role.ADMIN.name()
                                )
                                .requestMatchers("/momo/**").permitAll() // Bỏ xác thực cho API MoMo
                                .anyRequest().permitAll()
                        )
                        .csrf(csrf -> csrf.disable());

                return http.build();
        }


        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://10.0.2.2:3000", "http://10.0.2.2")); // Đảm bảo trùng với FE
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
