package server.rest.api_moodle.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.rest.api_moodle.entities.Maestro;

@Repository
public interface MaestroRepository extends JpaRepository<Maestro, Integer> {
    boolean existsByEmail(String email);
}