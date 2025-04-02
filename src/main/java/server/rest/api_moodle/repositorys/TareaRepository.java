package server.rest.api_moodle.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.rest.api_moodle.entities.Curso;
import server.rest.api_moodle.entities.Tarea;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {
    List<Tarea> findByCurso(Curso curso);
}
