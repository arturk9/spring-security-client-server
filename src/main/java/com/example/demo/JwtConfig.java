package com.example.demo;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
class JwtConfig {

        @Bean
        ReactiveJwtDecoder jwtPrefetchingDecoderByJwkKeySetUri(OAuth2ResourceServerProperties properties) {
            var props = properties.getJwt();
            return NimbusReactiveJwtDecoder.withJwkSetUri(props.getJwkSetUri())
                    .jwsAlgorithm(SignatureAlgorithm.from(props.getJwsAlgorithm()))
                    .build();
        }
}