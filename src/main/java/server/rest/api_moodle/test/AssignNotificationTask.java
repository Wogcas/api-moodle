package server.rest.api_moodle.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import server.rest.api_moodle.moodle.MoodleClient;
import server.rest.api_moodle.exceptions.MoodleApiException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AssignNotificationTask {

    private static final Logger log = LoggerFactory.getLogger(AssignNotificationTask.class);

    @Autowired
    private MoodleClient moodleClient;

    private Instant lastPollTime = Instant.now().minus(Duration.ofMinutes(15));

    @Scheduled(fixedRate = 360000) // 6 minutos en milisegundos
    public void checkNewSubmitions(){
        try {
            log.info("====== STARTING SUBMISSION CHECK ======");
            List<Map<String, Object>> assignments = getActiveAssignments();
            processAssignments(assignments);
            lastPollTime = Instant.now();
            log.info("====== SUBMISSION CHECK COMPLETED ======");
        } catch (Exception ex){
            log.error("====== ERROR IN SUBMISSION CHECK ======");
            throw new MoodleApiException("Failed to poll Moodle API", ex.getMessage());
        }
    }

    /**
     * Método público para permitir la invocación manual desde un controlador REST
     */
    public void manualCheck() {
        log.info("Manual check requested");
        checkNewSubmitions();
    }


    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getActiveAssignments() {
        Map<String, String> params = new HashMap<>();
        try {
            // Llamada a la API de Moodle para obtener todas las asignaciones
            Map<String, Object> response = moodleClient.executeGetRequest(
                    "mod_assign_get_assignments",
                    params,
                    Map.class
            );
            List<Map<String, Object>> result = new ArrayList<>();

            // La respuesta tiene un formato específico que necesitamos procesar
            if (response.containsKey("courses")) {
                List<Map<String, Object>> courses = (List<Map<String, Object>>) response.get("courses");

                for (Map<String, Object> course : courses) {
                    if (course.containsKey("assignments")) {
                        List<Map<String, Object>> courseAssignments =
                                (List<Map<String, Object>>) course.get("assignments");
                        result.addAll(courseAssignments);
                    }
                }
            }

            return result;
        } catch (Exception ex) {
            log.error("Error retrieving assignments: {}", ex.getMessage(), ex);
            throw new MoodleApiException("Failed to get active assignments", ex.getMessage());
        }
    }

    private void processAssignments(List<Map<String, Object>> assignments) {
        for (Map<String, Object> assignment : assignments) {
            try {
                Integer sendNotifications = (Integer) assignment.getOrDefault("sendnotifications", 0);

                if (sendNotifications == 1) {
                    checkRecentSubmissions(assignment);
                }
            } catch (Exception ex) {
                log.error("Error processing assignment {}: {}",
                        assignment.get("name"), ex.getMessage(), ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void checkRecentSubmissions(Map<String, Object> assignment) {
        try {

            Integer assignmentId = (Integer) assignment.get("id");

            Map<String, String> params = new HashMap<>();
            params.put("assignmentids[0]", assignmentId.toString());

            Map<String, Object> response = moodleClient.executePostRequest(
                    "mod_assign_get_submissions",
                    params,
                    Map.class // Esperamos un Map como respuesta
            );

            // Extraer los envíos reales de la respuesta
            List<Map<String, Object>> submissions = new ArrayList<>();
            if (response != null && response.containsKey("assignments") && response.get("assignments") instanceof List) {
                List<Map<String, Object>> assignmentsResponse = (List<Map<String, Object>>) response.get("assignments");
                if (!assignmentsResponse.isEmpty() && assignmentsResponse.get(0).containsKey("submissions") && assignmentsResponse.get(0).get("submissions") instanceof List) {
                    submissions = (List<Map<String, Object>>) assignmentsResponse.get(0).get("submissions");
                }
            }

            if (submissions.isEmpty()) {
                log.info("No submissions found for assignment '{}'.", assignment.get("name"));
            } else {
                // Procesar cada envío
                for (Map<String, Object> submission : submissions) {
                    processSubmission(assignment, submission);
                }
            }

        } catch (Exception ex) {
            log.error("Error checking submissions for assignment {}: {}",
                    assignment.get("name"), ex.getMessage(), ex);
        }
    }

    private void processSubmission(Map<String, Object> assignment, Map<String, Object> submission) {
        try {
            // Verificar si el envío es reciente (posterior a nuestra última consulta)
            Long timeModified = Long.parseLong(submission.getOrDefault("timemodified", "0").toString());
            Instant submissionTime = Instant.ofEpochSecond(timeModified);

            if (submissionTime.isAfter(lastPollTime)) {
                log.info("Found new submission for assignment '{}' by user {}",
                        assignment.get("name"), submission.get("userid"));

                // El envío es nuevo, enviar a RabbitMQ
//                sendToRabbitMQ(assignment, submission);


            }
        } catch (Exception ex) {
            log.error("Error processing submission: {}", ex.getMessage(), ex);
        }
    }
}