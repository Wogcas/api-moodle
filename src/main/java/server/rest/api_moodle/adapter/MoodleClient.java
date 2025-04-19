package server.rest.api_moodle.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class MoodleClient {

    @Value("${moodle.api.url}")
    private String moodleAPI;

    @Value("${moodle.api.token}")
    private String moodleToken;

    private final RestTemplate restTemplate;

    public MoodleClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public <T> T executeRequest(String wsfunction, Map<String, String> params, Class<T> responseType){
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

    }

}
