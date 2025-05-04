package server.rest.api_moodle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.moodle.Adapter;
import server.rest.api_moodle.dtos.AsignacionDTO;
import server.rest.api_moodle.entities.Alumno;
import server.rest.api_moodle.entities.Asignacion;
import server.rest.api_moodle.entities.Curso;
import server.rest.api_moodle.entities.Tarea;
import server.rest.api_moodle.repositorys.AlumnoRepository;
import server.rest.api_moodle.repositorys.AsignacionRepository;
import server.rest.api_moodle.repositorys.CursoRepository;
import server.rest.api_moodle.repositorys.TareaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AsignacionService {

    @Autowired
    private AsignacionRepository asignacionRepo;

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Autowired
    private TareaRepository tareaRepo;

    @Autowired
    private CursoRepository cursoRepo;

    public boolean crearAsignacion(AsignacionDTO asignacionDTO){
        try {
            Curso curso = cursoRepo.findById(asignacionDTO.getCursoId())
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

            List<Alumno> alumnos = curso.getAlumnos();
            for (Alumno alumno : alumnos) {
                Asignacion asignacion = Adapter.toEntity(asignacionDTO);
                asignacion.setAlumno(alumno);
                asignacion.setCurso(curso);
                asignacionRepo.save(asignacion);
            }

            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error en el servidor", ex);
        }
    }

    public List<AsignacionDTO> obtenerTodasLasAsignaciones() {
        List<Asignacion> asignaciones = asignacionRepo.findAll();
        return asignaciones.stream()
                .map(Adapter::toDTO)
                .collect(Collectors.toList());
    }

    public List<AsignacionDTO> obtenerAsignacionesPorAlumno(int alumnoId) {
        try {
            Alumno alumno = alumnoRepo.findById(alumnoId)
                    .orElseThrow(() -> new Exception("El alumno no existe"));

            List<Asignacion> asignaciones = asignacionRepo.findByAlumno(alumno);

            return asignaciones.stream()
                    .map(Adapter::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener asignaciones del alumno: " + ex.getMessage(), ex);
        }
    }

    public List<AsignacionDTO> obtenerAsignacionesPorCurso(int cursoId) {
        try {
            Curso curso = cursoRepo.findById(cursoId)
                    .orElseThrow(() -> new Exception("El curso no existe"));

            List<Asignacion> asignaciones = asignacionRepo.findByCurso(curso);

            return asignaciones.stream()
                    .map(Adapter::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener asignaciones del curso: " + ex.getMessage(), ex);
        }
    }

    public boolean calificarTarea(int tareaId, double nota) {
        try {
            Tarea tarea = tareaRepo.findById(tareaId)
                    .orElseThrow(() -> new Exception("La asignación no existe"));

            if (nota < 0 || nota > 10) {
                throw new Exception("La nota debe estar entre 0 y 10");
            }

            tarea.setNota(nota);
            tareaRepo.save(tarea);

            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error al calificar la asignación: " + ex.getMessage(), ex);
        }
    }

}
