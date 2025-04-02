package server.rest.api_moodle.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.rest.api_moodle.entities.Alumno;
import server.rest.api_moodle.entities.Asignacion;
import server.rest.api_moodle.entities.Tarea;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {

    List<Asignacion> findByAlumno(Alumno alumno);
    List<Asignacion> findByTarea(Tarea tarea);
}
