package server.rest.api_moodle.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.ResponseMapper;
import server.rest.api_moodle.moodle.MoodleClient;
import server.rest.api_moodle.domain.MoodleSiteInfo;
import server.rest.api_moodle.domain.ReportGradeCourseFromStudentInfo;

import java.util.List;
import java.util.Map;

@Service
public class MoodleTestService {

    @Autowired
    private MoodleClient moodleClient;


    @Autowired
    private ResponseMapper reportGradeCourseInfoMapper;

    public MoodleSiteInfo getSiteInfo(Map<String, String> params){
        return moodleClient.executeGetRequest("core_webservice_get_site_info", params, MoodleSiteInfo.class);
    }

    public List<Object> getCourses(Map<String, String> params){
        return moodleClient.executeGetRequestForList("core_course_get_courses", params, Object.class);
    }

    public List<Object> getUserCourses(Map<String, String> params){
        return moodleClient.executeGetRequestForList("core_enrol_get_users_courses", params, Object.class);
    }

    public ReportGradeCourseFromStudentInfo getFinalGradeFromCourse(Map<String, String> params){
        String jsonResponse = moodleClient.executeGetRequest(
                "gradereport_user_get_grades_table",
                params,
                String.class // Obtén la respuesta como String
        );
        return reportGradeCourseInfoMapper.map(jsonResponse);

    }
    
}
