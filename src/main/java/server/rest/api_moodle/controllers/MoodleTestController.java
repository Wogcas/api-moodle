package server.rest.api_moodle.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import server.rest.api_moodle.domain.MoodleSiteInfo;
import server.rest.api_moodle.domain.ReportGradeCourseFromStudentInfo;
import server.rest.api_moodle.test.AssignNotificationTask;
import server.rest.api_moodle.test.MoodleTestService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/moodle/rest")
public class MoodleTestController {

    private static final Logger log = LoggerFactory.getLogger(MoodleTestController.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @Autowired
    private MoodleTestService moodleTestService;

    @Autowired
    private AssignNotificationTask assignNotificationTask;

    @GetMapping("/site-info")
    public ResponseEntity<MoodleSiteInfo> testConnection() {
        Map<String, String> params = new HashMap<>();
        MoodleSiteInfo siteInfo = moodleTestService.getSiteInfo(params);
        return ResponseEntity.ok(siteInfo);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Object>> getCourses(){
        Map<String, String> params = new HashMap<>();
        List<Object> courses = moodleTestService.getCourses(params);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/users/{userId}/courses")
    public ResponseEntity<List<Object>> getUserCourses(@PathVariable Integer userId) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", String.valueOf(userId));
        List<Object> userCourses = moodleTestService.getUserCourses(params);
        return ResponseEntity.ok(userCourses);
    }

    @GetMapping("/user/{userId}/course/{courseId}/grades")
    public ResponseEntity<ReportGradeCourseFromStudentInfo> getCourseFinalGrade(
            @PathVariable Integer userId,
            @PathVariable Integer courseId) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", String.valueOf(userId));
        params.put("courseid", String.valueOf(courseId));
        ReportGradeCourseFromStudentInfo finalCourseGrade = moodleTestService.getFinalGradeFromCourse(params);
        return ResponseEntity.ok(finalCourseGrade);
    }

    /**
     * Endpoint para verificar manualmente nuevos envíos de tareas
     * @return Respuesta con información de la verificación
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, String>> checkAssignments() {
        log.info("Manual check requested via API endpoint");

        Map<String, String> response = new HashMap<>();
        Instant startTime = Instant.now();

        try {
            // Ejecutar la verificación manual
            assignNotificationTask.manualCheck();

            Instant endTime = Instant.now();
            long durationMs = endTime.toEpochMilli() - startTime.toEpochMilli();

            response.put("status", "success");
            response.put("message", "Manual check completed successfully");
            response.put("startTime", formatter.format(startTime));
            response.put("endTime", formatter.format(endTime));
            response.put("durationMs", String.valueOf(durationMs));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during manual check: {}", e.getMessage(), e);

            response.put("status", "error");
            response.put("message", "Error during manual check: " + e.getMessage());
            response.put("startTime", formatter.format(startTime));
            response.put("endTime", formatter.format(Instant.now()));

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint para verificar el estado del servicio de notificaciones
     * @return Estado del servicio
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        log.info("Status check requested");

        Map<String, String> status = new HashMap<>();
        status.put("service", "Moodle Notification Service");
        status.put("status", "active");
        status.put("currentTime", formatter.format(Instant.now()));

        return ResponseEntity.ok(status);
    }

    @GetMapping("/users/{userId}/assignments")
    public ResponseEntity<List<Object>> getAssignments(@PathVariable Integer userId){
        List<Object> assignments = new ArrayList<>();
        return ResponseEntity.ok(assignments);
    }
}
