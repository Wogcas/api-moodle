package server.rest.api_moodle.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.rest.api_moodle.entities.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {

    boolean existsByNombre(String nombre);

}
