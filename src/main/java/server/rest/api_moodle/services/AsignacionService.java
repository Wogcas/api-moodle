package server.rest.api_moodle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.Adapter;
import server.rest.api_moodle.dtos.AsignacionDTO;
import server.rest.api_moodle.entities.Alumno;
import server.rest.api_moodle.entities.Asignacion;
import server.rest.api_moodle.entities.Curso;
import server.rest.api_moodle.entities.Tarea;
import server.rest.api_moodle.repositorys.AlumnoRepository;
import server.rest.api_moodle.repositorys.AsignacionRepository;
import server.rest.api_moodle.repositorys.CursoRepository;
import server.rest.api_moodle.repositorys.TareaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
            Alumno alumno = alumnoRepo.findById(asignacionDTO.getAlumnoId())
                    .orElseThrow(() -> new Exception("El alumno no existe"));

            Tarea tarea = tareaRepo.findById(asignacionDTO.getTareaId())
                    .orElseThrow(() -> new Exception("La tarea no existe"));

            boolean alumnoEnCurso = alumno.getCursos().stream()
                    .anyMatch(curso -> curso.getId() == tarea.getCurso().getId());

            if (!alumnoEnCurso) {
                throw new Exception("El alumno no está inscrito en el curso de esta tarea");
            }

            Asignacion asignacion = Adapter.toEntity(asignacionDTO);
            asignacion.setAlumno(alumno);
            asignacion.setTarea(tarea);
            asignacion.setFechaPublicacion(LocalDateTime.now());

            asignacionRepo.save(asignacion);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error en el servidor", e);
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
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener asignaciones del alumno: " + e.getMessage(), e);
        }
    }

    public List<AsignacionDTO> obtenerAsignacionesPorTarea(int tareaId) {
        try {
            Tarea tarea = tareaRepo.findById(tareaId)
                    .orElseThrow(() -> new Exception("La tarea no existe"));

            List<Asignacion> asignaciones = asignacionRepo.findByTarea(tarea);

            return asignaciones.stream()
                    .map(Adapter::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener asignaciones de la tarea: " + e.getMessage(), e);
        }
    }

    public List<AsignacionDTO> obtenerAsignacionesPorCurso(int cursoId) {
        try {
            Curso curso = cursoRepo.findById(cursoId)
                    .orElseThrow(() -> new Exception("El curso no existe"));

            List<Tarea> tareas = tareaRepo.findByCurso(curso);

            List<Asignacion> asignaciones = new ArrayList<>();
            for (Tarea tarea : tareas) {
                asignaciones.addAll(asignacionRepo.findByTarea(tarea));
            }

            return asignaciones.stream()
                    .map(Adapter::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener asignaciones del curso: " + e.getMessage(), e);
        }
    }

    public boolean calificarAsignacion(int asignacionId, double nota) {
        try {
            Asignacion asignacion = asignacionRepo.findById(asignacionId)
                    .orElseThrow(() -> new Exception("La asignación no existe"));

            if (nota < 0 || nota > 10) {
                throw new Exception("La nota debe estar entre 0 y 10");
            }

            asignacion.setNota(nota);
            asignacionRepo.save(asignacion);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al calificar la asignación: " + e.getMessage(), e);
        }
    }

}
