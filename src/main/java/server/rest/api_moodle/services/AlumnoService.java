package server.rest.api_moodle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.Adapter;
import server.rest.api_moodle.dtos.AlumnoDTO;
import server.rest.api_moodle.entities.Alumno;
import server.rest.api_moodle.repositorys.AlumnoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepo;

    public boolean crearAlumno(AlumnoDTO alumnoDTO){
        try {
            boolean alumnoExistente = alumnoRepo.existsByEmail(alumnoDTO.getEmail());
            if (alumnoExistente) {
                throw new Exception("El alumno con este email ya existe en el servidor");
            }
            Alumno alumno = Adapter.toEntity(alumnoDTO);
            alumnoRepo.save(alumno);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error en el servidor", e);
        }
    }

    public List<AlumnoDTO> obtenerTodosLosAlumnos() {
        List<Alumno> alumnos = alumnoRepo.findAll();
        return alumnos.stream()
                .map(Adapter::toDTO)
                .collect(Collectors.toList());
    }

}
