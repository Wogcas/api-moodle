package server.rest.api_moodle.exceptions;

public class MoodleApiException extends RuntimeException {
    public MoodleApiException(String message, String exMessage) {
        super(message);
    }
}
