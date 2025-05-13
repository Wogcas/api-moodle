package server.rest.api_moodle.moodle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import server.rest.api_moodle.exceptions.MoodleApiException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.rmi.server.LogStream.log;

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
     * Ejecuta una petición GET a Moodle para un objeto.
     * @param <T> Tipo de la respuesta.
     * @param wsfunction Función de Moodle.
     * @param params Parámetros adicionales.
     * @param responseType Clase del tipo de respuesta.
     * @return Objeto de tipo T con la respuesta.
     * @throws MoodleApiException Si ocurre un error.
     */
    public <T> T executeGetRequest(String wsfunction, Map<String, String> params, Class<T> responseType) {
        return executeHttpRequest(HttpMethod.GET, wsfunction, params, null, responseType);
    }

    /**
     * Ejecuta una petición GET a Moodle esperando una lista de objetos.
     * Deserializa manualmente la respuesta JSON.
     * @param <T> Tipo de los elementos de la lista.
     * @param wsfunction Función de Moodle.
     * @param params Parámetros adicionales.
     * @param elementType Clase del tipo de los elementos de la lista.
     * @return Lista de objetos de tipo T.
     * @throws MoodleApiException Si hay un error de IO o cualquier otra excepción.
     */
    public <T> List<T> executeGetRequestForList(String wsfunction, Map<String, String> params, Class<T> elementType) {
        try {
            UriComponentsBuilder builder = buildUri(wsfunction, params);
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class
            );
            System.out.println("Respuesta de Moodle: " + response.getBody());
            return objectMapper.readValue(response.getBody(), new TypeReference<List<T>>() {});
        } catch (IOException ex) {
            throw new MoodleApiException("Failed to execute Moodle API GET request for list", ex.getMessage());
        } catch (Exception ex) {
            throw new MoodleApiException("Failed to execute Moodle API GET request", ex.getMessage());
        }
    }

    /**
     * Ejecuta una petición POST a Moodle con x-www-form-urlencoded.
     * @param <T> Tipo de la respuesta.
     * @param wsfunction Función de Moodle.
     * @param params Parámetros para el body.
     * @param responseType Clase del tipo de respuesta.
     * @return Objeto de tipo T con la respuesta.
     * @throws MoodleApiException Si ocurre un error.
     */
    public <T> T executePostRequest(String wsfunction, Map<String, String> params, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("wstoken", moodleToken);
        body.add("wsfunction", wsfunction);
        body.add("moodlewsrestformat", "json");
        if (params != null) {
            params.forEach(body::add);
        }

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // Crear una copia del body para loggear sin el wstoken
        MultiValueMap<String, String> logBody = new LinkedMultiValueMap<>(body);
        logBody.remove("wstoken");

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    moodleAPI,
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );
            return response.getBody();
        } catch (Exception ex) {
            throw new MoodleApiException("Failed to execute Moodle API POST request", ex.getMessage());
        }
    }

    /**
     * Construye la URI para la petición GET a Moodle.
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
     * Método genérico para ejecutar peticiones HTTP (ahora solo usado para GET).
     */
    private <T> T executeHttpRequest(HttpMethod method, String wsfunction, Map<String, String> params, HttpEntity<?> requestEntity, Class<T> responseType) {
        try {
            UriComponentsBuilder builder = buildUri(wsfunction, params);
            String uri = builder.toUriString();
            ResponseEntity<T> response = restTemplate.exchange(
                    uri,
                    method,
                    requestEntity,
                    responseType
            );
            return response.getBody();
        } catch (Exception ex) {
            throw new MoodleApiException("Failed to execute Moodle API request", ex.getMessage());
        }
    }
}