package server.rest.api_moodle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiMoodleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiMoodleApplication.class, args);
		System.out.println("Servidor activo, en ejucucion y listo. ");
	}

}
