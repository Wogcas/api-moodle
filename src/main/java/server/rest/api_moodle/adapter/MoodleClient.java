package server.rest.api_moodle.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;
import server.rest.api_moodle.exceptions.MoodleApiException;

import java.time.Duration;
import java.util.Map;

@Component
public class MoodleClient {

    private static final Logger log = LoggerFactory.getLogger(MoodleClient.class);

    @Value("${moodle.api.url}")
    private String moodleAPI;

    @Value("${moodle.api.token}")
    private String moodleToken;

    private final RestTemplate restTemplate;

    public MoodleClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .build();
    }

    public <T> T executeRequest(String wsfunction, Map<String, String> params, Class<T> responseType){
        try{
            HttpHeaders headers = new HttpHeaders();

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(moodleAPI)
                    .queryParam("wstoken", moodleToken)
                    .queryParam("wsfunction", wsfunction)
                    .queryParam("moodlewsrestformat", "json");

            if (params != null){
                params.forEach(builder::queryParam);
            }

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<T> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    responseType);

            return response.getBody();

        } catch (Exception ex) {
            throw new MoodleApiException("Failed to execute Moodle API request", ex.getMessage());
        }
    }

}
