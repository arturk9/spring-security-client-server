package com.example.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"spring.security.oauth2.client.registration.service.client-secret=secret",
				"spring.security.oauth2.client.registration.service.client-id=correctScope",
				"spring.security.oauth2.client.registration.service.authorization-grant-type=client_credentials"
		},
		classes = DemoApplication.class
)
class DemoApplicationTests {

	@Value("${local.server.port}")
	private int appPort;

	@BeforeAll
	static void init () {
		externalAuthServiceMock.start();
	}

	@AfterAll
	static void teardown() {
		externalAuthServiceMock.close();
	}

	@Test
	void reproduceIssue() {
		var registration = ClientRegistration
				.withRegistrationId("clientRegistrationId")
				.tokenUri(ExternalAuthServiceMock.oauth2Uri)
				.clientId("correctScope")
				.clientSecret("secret")
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.build();

		var clientRegistration = new InMemoryReactiveClientRegistrationRepository(registration);

		var clientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistration);
		var authorizedClientManager =
				new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistration, clientService);

		var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
		oauth.setDefaultClientRegistrationId("clientRegistrationId");

		var statusCode = WebClient.builder()
				.baseUrl(String.format("http://127.0.0.1:%s/helloworld", appPort))
				.filter(oauth)
				.build()
				.post()
				.bodyValue(new Model("name", 18))
				.exchangeToMono(response -> Mono.just(response.statusCode()))
				.block();

		assert statusCode.value() == 200;
	}

	@Test
	void inconsistencyWhenSideEffect() {
		var registration = ClientRegistration
				.withRegistrationId("clientRegistrationId")
				.tokenUri(ExternalAuthServiceMock.oauth2Uri)
				.clientId("correctScope")
				.clientSecret("secret")
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.build();

		var clientRegistration = new InMemoryReactiveClientRegistrationRepository(registration);

		var clientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistration);
		var authorizedClientManager =
				new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistration, clientService);

		var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
		oauth.setDefaultClientRegistrationId("clientRegistrationId");

		var statusCode = WebClient.builder()
				.baseUrl(String.format("http://127.0.0.1:%s/helloworldnoctx", appPort))
				.filter(oauth)
				.build()
				.post()
				.bodyValue(new Model("name", 18))
				.exchangeToMono(response -> Mono.just(response.statusCode()))
				.block();

		assert statusCode.value() == 200;
	}

	private static final ExternalAuthServiceMock externalAuthServiceMock = new ExternalAuthServiceMock();

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> ExternalAuthServiceMock.jwksUri);
		registry.add("spring.security.oauth2.client.provider.service.token-uri", () -> ExternalAuthServiceMock.oauth2Uri);
	}
}
