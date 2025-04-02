package server.rest.api_moodle.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import server.rest.api_moodle.entities.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {}
