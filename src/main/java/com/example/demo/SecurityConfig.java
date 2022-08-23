package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorityReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http)  {
        http
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .csrf()
                .disable()
                .headers(it -> it.contentSecurityPolicy(contentSecurityPolicy ->
                        contentSecurityPolicy
                                .policyDirectives("default-src 'none'; script-src 'none'; object-src 'none'; base-uri 'none'; require-trusted-types-for 'script';")))
                .authorizeExchange (it -> it.pathMatchers("/helloworld").access(AuthorityReactiveAuthorizationManager.hasAuthority("SCOPE_myscope")))
                .authorizeExchange (it -> it.pathMatchers("/helloworldnoctx").access(AuthorityReactiveAuthorizationManager.hasAuthority("SCOPE_myscope")))
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

        return http.build();
    }
}
