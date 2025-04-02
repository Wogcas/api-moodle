package server.rest.api_moodle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.Adapter;
import server.rest.api_moodle.dtos.AlumnoDTO;
import server.rest.api_moodle.dtos.CursoDTO;
import server.rest.api_moodle.entities.Alumno;
import server.rest.api_moodle.entities.Curso;
import server.rest.api_moodle.repositorys.AlumnoRepository;
import server.rest.api_moodle.repositorys.CursoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Autowired
    private CursoRepository cursoRepo;

    public boolean crearAlumno(AlumnoDTO alumnoDTO){
        try {
            boolean alumnoExistente = alumnoRepo.findByEmail(alumnoDTO.getEmail());
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

    public boolean inscribirAlumnoEnCurso(int alumnoId, int cursoId) {
        try {
            Alumno alumno = alumnoRepo.findById(alumnoId)
                    .orElseThrow(() -> new Exception("El alumno no existe"));

            Curso curso = cursoRepo.findById(cursoId)
                    .orElseThrow(() -> new Exception("El curso no existe"));

            if (alumno.getCursos() != null && alumno.getCursos().contains(curso)) {
                return false; // Ya está inscrito
            }

            if (alumno.getCursos() == null) {
                alumno.setCursos(new ArrayList<>());
            }

            alumno.getCursos().add(curso);
            alumnoRepo.save(alumno);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al inscribir alumno en curso: " + e.getMessage(), e);
        }

    }

    public List<CursoDTO> obtenerCursosDeAlumno(int alumnoId) {
        try {
            Alumno alumno = alumnoRepo.findById(alumnoId)
                    .orElseThrow(() -> new Exception("El alumno no existe"));

            if (alumno.getCursos() != null) {
                return alumno.getCursos().stream()
                        .map(Adapter::toDTO)
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener cursos del alumno: " + e.getMessage(), e);
        }
    }

    public List<AlumnoDTO> obtenerAlumnosDeCurso(int cursoId) {
        try {
            Curso curso = cursoRepo.findById(cursoId)
                    .orElseThrow(() -> new Exception("El curso no existe"));

            if (curso.getAlumnos() != null) {
                return curso.getAlumnos().stream()
                        .map(Adapter::toDTO)
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener alumnos del curso: " + e.getMessage(), e);
        }
    }

    public boolean eliminarInscripcionAlumno(int alumnoId, int cursoId) {
        try {
            Alumno alumno = alumnoRepo.findById(alumnoId)
                    .orElseThrow(() -> new Exception("El alumno no existe"));

            Curso curso = cursoRepo.findById(cursoId)
                    .orElseThrow(() -> new Exception("El curso no existe"));

            if (alumno.getCursos() == null || !alumno.getCursos().contains(curso)) {
                return false;
            }

            alumno.getCursos().remove(curso);
            alumnoRepo.save(alumno);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar inscripción del alumno: " + e.getMessage(), e);
        }
    }

}
