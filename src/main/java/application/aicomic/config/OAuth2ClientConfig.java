package application.aicomic.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties) {
        List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration().entrySet().stream()
                .map(entry -> {
                    OAuth2ClientProperties.Registration registration = entry.getValue();
                    OAuth2ClientProperties.Provider provider = oAuth2ClientProperties.getProvider().get(entry.getKey());

                    return ClientRegistration.withRegistrationId(entry.getKey())
                            .clientId(registration.getClientId())
                            .clientSecret(registration.getClientSecret())
                            .scope(registration.getScope().toArray(new String[0]))
                            .authorizationUri(provider.getAuthorizationUri())
                            .tokenUri(provider.getTokenUri())
                            .userInfoUri(provider.getUserInfoUri())
                            .jwkSetUri(provider.getJwkSetUri())
                            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                            .redirectUri(registration.getRedirectUri())
                            .build();
                })
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }
}
