package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    LogProcessor logProcessor(
            WebClient.Builder builder,
            @Value("${spring.security.oauth2.client.provider.service.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.registration.service.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.service.client-secret}") String clientSecret,
            @Value("${spring.security.oauth2.client.registration.service.authorization-grant-type}") String authorizationGrantType) {

        var registration = ClientRegistration
                .withRegistrationId("clientRegistrationId")
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();

        var clientRegistration = new InMemoryReactiveClientRegistrationRepository(registration);

        var clientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistration);
        var authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistration, clientService);

        var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultClientRegistrationId("clientRegistrationId");

        var client =  WebClient.builder()
                .baseUrl("http://google.com")
                .filter(oauth)
                .build();

        return new LogProcessor(client);
    }
}
