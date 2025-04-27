package server.rest.api_moodle.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MoodleSiteInfo(
        String sitename,
        String siteurl,
        Long userid,
        String username,
        String fullname,
        List<WebServiceFunction> functions,
        boolean userissiteadmin
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WebServiceFunction(
            String name,
            String version
    ) {}
}
