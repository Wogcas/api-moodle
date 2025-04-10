package server.rest.api_moodle.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.rest.api_moodle.entities.Tarea;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {}
