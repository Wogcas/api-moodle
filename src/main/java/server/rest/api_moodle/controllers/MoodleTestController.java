package server.rest.api_moodle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import server.rest.api_moodle.domain.MoodleSiteInfo;
import server.rest.api_moodle.test.MoodleTestService;

@Controller
@RequestMapping("/api/moodle/rest")
public class MoodleTestController {

    @Autowired
    private MoodleTestService moodleTestService;

    @GetMapping("/site-info")
    public ResponseEntity<MoodleSiteInfo> testConnection() {
        MoodleSiteInfo siteInfo = moodleTestService.getSiteInfo();
        return ResponseEntity.ok(siteInfo);
    }
}
