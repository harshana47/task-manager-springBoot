package org.example.taskmanager03.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuoteService {

    private final RestTemplate restTemplate;
    private static final String QUOTE_API_URL = "https://api.adviceslip.com/advice";

    public String getMotivationalQuote() {
        log.info("Fetching motivational quote from {}", QUOTE_API_URL);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(MediaType.parseMediaTypes("application/json"));
            headers.set(HttpHeaders.USER_AGENT, "TaskManager/1.0 (+https://example.org)");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    QUOTE_API_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String response = responseEntity.getBody();
            if (response == null || response.isBlank()) {
                log.warn("Quote API returned empty response (status: {}, contentType: {})",
                        responseEntity.getStatusCode(), responseEntity.getHeaders().getContentType());
                return "Stay positive and keep coding!";
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(response);
                if (root.has("slip") && root.get("slip").has("advice")) {
                    String advice = root.get("slip").get("advice").asText();
                    log.debug("Parsed advice from JSON.");
                    return advice;
                }
                if (root.has("advice")) {
                    String advice = root.get("advice").asText();
                    log.debug("Parsed advice from JSON (flat).");
                    return advice;
                }
                log.warn("Quote API JSON did not contain expected fields. Returning raw body.");
            } catch (Exception ex) {
                log.warn("Failed to parse Quote API JSON response, returning raw response. Error: {}", ex.getMessage());
            }

            return response.trim();
        } catch (Exception e) {
            log.error("Error calling Quote API: {}", e.getMessage(), e);
            return "Keep pushing forward!";
        }
    }
}
