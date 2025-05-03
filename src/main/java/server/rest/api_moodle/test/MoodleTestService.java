package server.rest.api_moodle.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.MoodleClient;
import server.rest.api_moodle.domain.MoodleSiteInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MoodleTestService {

    @Autowired
    private MoodleClient moodleClient;

    public MoodleSiteInfo getSiteInfo(Map<String, String> params){
        return moodleClient.executeRequest("core_webservice_get_site_info", params, MoodleSiteInfo.class);
    }

    public List<Object> getCourses(Map<String, String> params){
        return moodleClient.executeRequestForList("core_course_get_courses", params, Object.class);
    }

    public List<Object> getUserCourses(Map<String, String> params){
        return moodleClient.executeRequestForList("core_enrol_get_users_courses", params, Object.class);
    }
    
}
