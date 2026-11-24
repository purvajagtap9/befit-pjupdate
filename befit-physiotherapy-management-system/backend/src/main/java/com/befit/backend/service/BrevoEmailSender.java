package com.befit.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Component
public class BrevoEmailSender implements EmailSender {
    private final RestClient client = RestClient.create();
    @Value("${mail.api-key}") private String apiKey;
    @Value("${mail.from}") private String from;
    @Value("${mail.provider-url}") private String url;

    @Override
    public void send(String to, String subject, String text) {
        Map<String, Object> body = Map.of(
            "sender", Map.of("email", from, "name", "BeFit Physiotherapy"),
            "to", List.of(Map.of("email", to)),
            "subject", subject,
            "textContent", text);
        client.post().uri(url)
              .header("api-key", apiKey)
              .contentType(MediaType.APPLICATION_JSON)
              .body(body)
              .retrieve()
              .toBodilessEntity();
    }
}