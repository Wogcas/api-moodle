package server.rest.api_moodle.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class MoodleClient {

    private static final Logger log = LoggerFactory.getLogger(MoodleClient.class);

    @Value("${moodle.api.url}")
    private String moodleAPI;

    @Value("${moodle.api.token}")
    private String moodleToken;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MoodleClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Ejecuta una petición a Moodle para un objeto.
     * @param <T> Tipo de la respuesta.
     * @param wsfunction Función de Moodle.
     * @param params Parámetros adicionales.
     * @param responseType Clase del tipo de respuesta.
     * @return Objeto de tipo T con la respuesta.
     * @throws MoodleApiException Si ocurre un error.
     */
    public <T> T executeRequest(String wsfunction, Map<String, String> params, Class<T> responseType) {
        return executeHttpRequest(wsfunction, params, responseType);
    }

    /**
     * Ejecuta una petición a Moodle esperando una lista de objetos.
     * Deserializa manualmente la respuesta JSON.
     * @param <T> Tipo de los elementos de la lista.
     * @param wsfunction Función de Moodle.
     * @param params Parámetros adicionales.
     * @param elementType Clase del tipo de los elementos de la lista.
     * @return Lista de objetos de tipo T.
     * @throws MoodleApiException Si hay un error de IO o cualquier otra excepción.
     */
    public <T> List<T> executeRequestForList(String wsfunction, Map<String, String> params, Class<T> elementType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            UriComponentsBuilder builder = buildUri(wsfunction, params);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class // Recibimos la respuesta como String
            );

            // Deserializamos manualmente la lista usando Jackson
            return objectMapper.readValue(response.getBody(), new TypeReference<List<T>>() {});

        } catch (IOException ex) {
            throw new MoodleApiException("Failed to execute Moodle API request for list", ex.getMessage());
        } catch (Exception ex) {
            throw new MoodleApiException("Failed to execute Moodle API request", ex.getMessage());
        }
    }

    /**
     * Construye la URI para la petición a Moodle.
     * Incluye token, función y formato. Añade parámetros extra.
     * @param wsfunction Función de Moodle.
     * @param params Parámetros adicionales.
     * @return UriComponentsBuilder con la URI construida.
     */
    private UriComponentsBuilder buildUri(String wsfunction, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(moodleAPI)
                .queryParam("wstoken", moodleToken)
                .queryParam("wsfunction", wsfunction)
                .queryParam("moodlewsrestformat", "json");
        if (params != null) {
            params.forEach(builder::queryParam);
        }
        return builder;
    }

    /**
     * Ejecuta una petición GET a Moodle y deserializa la respuesta a un objeto.
     * @param <T> Tipo de la respuesta.
     * @param wsfunction Función de Moodle.
     * @param params Parámetros adicionales.
     * @param responseType Clase del tipo de respuesta.
     * @return Objeto de tipo T con la respuesta.
     * @throws MoodleApiException Si ocurre un error.
     */
    private <T> T executeHttpRequest(String wsfunction, Map<String, String> params, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            UriComponentsBuilder builder = buildUri(wsfunction, params);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<T> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    responseType
            );
            return response.getBody();
        } catch (Exception ex) {
            throw new MoodleApiException("Failed to execute Moodle API request", ex.getMessage());
        }
    }
}