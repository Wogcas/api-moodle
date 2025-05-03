package server.rest.api_moodle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import server.rest.api_moodle.domain.MoodleSiteInfo;
import server.rest.api_moodle.test.MoodleTestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/moodle/rest")
public class MoodleTestController {

    @Autowired
    private MoodleTestService moodleTestService;

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
}
