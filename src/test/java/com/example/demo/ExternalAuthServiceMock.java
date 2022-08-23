package com.example.demo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.io.Closeable;

public class ExternalAuthServiceMock implements Closeable {

    private static final int jwksServerPort = 8015;
    private static final String jwksPath = "/jwks";
    private static final String oauth2Path = "/oauth2";

    public static final String jwksUri = String.format("http://localhost:%s%s", jwksServerPort, jwksPath);
    public static final String oauth2Uri = String.format("http://localhost:%s%s", jwksServerPort, oauth2Path);

    private final WireMockServer jwkMockServer = new WireMockServer(
            WireMockConfiguration
                    .options()
                    .port(jwksServerPort)
    );

    void start() {
        WireMock.configureFor(jwksServerPort);
        jwkMockServer.start();
        jwkMockServer.addStubMapping(
                WireMock.stubFor(
                        WireMock.get(WireMock.urlPathEqualTo("/jwks"))
                                .willReturn(
                                        WireMock.aResponse()
                                                .withHeader("Content-Type", "application/json")
                                                .withStatus(200)
                                                .withBody("{\"keys\":[{\"kty\":\"RSA\",\"e\":\"AQAB\",\"kid\":\"idmSimKeyId\",\"n\":\"uJ5d8zo0j-FdsxXWqEBfrSToUouom12zJK5Z6QTKVtFOmdNJYiZ1xjsflPSo2lATxGB3euvpONRFG0AUs4HN__blm9SjDJUoW9q_nrcq6wAZH2NqB7dSqAr6EwZC-hkAEmvh8bKNho7RX6DdbVI6lfFNws1mwSUf6Orp1zzLLt8jp2E1SJOKZ4crDbCd-d0DInnVVuY7tl4z_NGTdYXMHQ0MKighfnF2L1rFMxwyC45ubotfKpHFKxjfOUWJIFrAYPS5gMG504uPjzMJ10w3Tdx08wX7wOr8_kcyFYnP18onbhsSkFRq44HEd--zJmoK8DpBZb08di0VKjg5aW7-aw\"}]}")
                                )
                )
        );
        jwkMockServer.addStubMapping(
                WireMock.stubFor(
                        WireMock.post(WireMock.urlPathEqualTo("/oauth2"))
                                .willReturn(
                                        WireMock.aResponse()
                                                .withHeader("Content-Type", "application/json")
                                                .withStatus(200)
                                                .withBody(
                                                        "{\n" +
                                                                "     \"access_token\": \"eyJraWQiOiJpZG1TaW1LZXlJZCIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiJsb2NhbGhvc3Q6MTI1ODIiLCJleHAiOjQ4MTY5MTk5OTAsImNsaWVudF9pZCI6ImNvcnJlY3RTY29wZSIsInNjb3BlIjoibXlzY29wZSJ9.J41mBzOb3ouoJbXt5-4fMh13UAsvAVtwgeY5z5XmrhxWq8PiY5hfPQcFXhzGKf8GIKDlbD2TYLcd13h21dTzTMZzUtJ-uhDVgs68Nv-6lRN400JSoQs0SNi33AqNch2wnEgwZSbFqZNkmWLvZv5d2kL3joNHJMxiweauHa4URkw1i6nyVqJMGscex0uAIciKCIn0Wo5HFnmdEjFM1rKcTc3piRlnVCfyo2GuXJgPbLdejarLrlFERdilGJgf6v-6Vw-Z48MGLhLAYlO6KkQPkXQs-reZ_O9tM-f3kOuRENtwt0HtiJQO9jTatUQezPnHDlhvP8mYuwKW3EcllHO0hQ\",\n" +
                                                                "     \"token_type\": \"bearer\"                               \n" +
                                                                "}"
                                                )
                                )
                )
        );
    }

    @Override
    public void close() {
        jwkMockServer.stop();
    }
}
