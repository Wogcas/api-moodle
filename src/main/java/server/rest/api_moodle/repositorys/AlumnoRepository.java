package server.rest.api_moodle.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.rest.api_moodle.entities.Alumno;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    boolean existsByEmail(String email);
}
