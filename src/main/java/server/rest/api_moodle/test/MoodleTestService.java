package server.rest.api_moodle.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.MoodleClient;
import server.rest.api_moodle.domain.MoodleSiteInfo;

import java.util.HashMap;
import java.util.Map;

@Service
public class MoodleTestService {

    @Autowired
    private MoodleClient moodleClient;

    public MoodleSiteInfo getSiteInfo(){
        Map<String, String> params = new HashMap<>();
        return moodleClient.executeRequest("core_webservice_get_site_info", params, MoodleSiteInfo.class);
    }
}
