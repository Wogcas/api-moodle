package server.rest.api_moodle.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.MoodleClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class MoodleTestService {

    @Autowired
    private MoodleClient moodleClient;

    public Object getSiteInfo(){
        Map<String, String> params = new HashMap<>();
        return moodleClient.executeRequest("core_webservice_get_site_info", params, Object.class);
    }
}
