package server.rest.api_moodle.adapter;

import server.rest.api_moodle.dtos.*;
import server.rest.api_moodle.entities.*;

import java.util.List;
import java.util.stream.Collectors;

public class Adapter {

    public static Curso toEntity(CursoDTO dto) {
        Curso curso = new Curso();
        curso.setNombre(dto.getNombre());
        return curso;
    }

    public static CursoDTO toDTO(Curso entity) {
        CursoDTO dto = new CursoDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());

        if (entity.getAlumnos() != null) {
            List<Integer> alumnosIds = entity.getAlumnos().stream()
                    .map(Alumno::getId)
                    .collect(Collectors.toList());
            dto.setAlumnosIds(alumnosIds);
        }

        if (entity.getAsignaciones() != null) {
            List<Integer> asignacionesIds = entity.getAsignaciones().stream()
                    .map(Asignacion::getId)
                    .collect(Collectors.toList());
            dto.setAsignacionesIds(asignacionesIds);
        }

        return dto;
    }

    public static Alumno toEntity(AlumnoDTO dto) {
        Alumno alumno = new Alumno();
        alumno.setNombre(dto.getNombre());
        alumno.setApellido(dto.getApellido());
        alumno.setEmail(dto.getEmail());
        alumno.setPassword(dto.getPassword());
        return alumno;
    }

    public static AlumnoDTO toDTO(Alumno entity) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setEmail(entity.getEmail());
        // La contraseña no se manda
        if (entity.getCursos() != null) {
            List<Integer> cursosIds = entity.getCursos().stream()
                    .map(Curso::getId)
                    .collect(Collectors.toList());
            dto.setCursosIds(cursosIds);
        }

        if (entity.getAsignaciones() != null) {
            List<Integer> asignacionesIds = entity.getAsignaciones().stream()
                    .map(Asignacion::getId)
                    .collect(Collectors.toList());
            dto.setAsignacionesIds(asignacionesIds);
        }

        return dto;
    }

    public static Maestro toEntity(MaestroDTO dto) {
        Maestro maestro = new Maestro();
        maestro.setNombre(dto.getNombre());
        maestro.setApellido(dto.getApellido());
        maestro.setEmail(dto.getEmail());
        maestro.setPassword(dto.getPassword());

        return maestro;
    }

    public static MaestroDTO toDTO(Maestro entity) {
        MaestroDTO dto = new MaestroDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setEmail(entity.getEmail());

        if (entity.getCursos() != null) {
            List<Integer> cursosIds = entity.getCursos().stream()
                    .map(Curso::getId)
                    .collect(Collectors.toList());
            dto.setCursosIds(cursosIds);
        }

        return dto;
    }

    public static Tarea toEntity(TareaDTO dto) {
        Tarea tarea = new Tarea();
        tarea.setTitulo(dto.getTitulo());
        tarea.setContenido(dto.getContenido());
        tarea.setNota(dto.getNota());
        tarea.setFechaEntrega(dto.getFechaEntrega());
        tarea.setFechaRevision(dto.getFechaRevision());
        return tarea;
    }

    public static TareaDTO toDTO(Tarea entity) {
        TareaDTO dto = new TareaDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setNota(entity.getNota());
        dto.setFechaEntrega(entity.getFechaEntrega());
        dto.setFechaRevision(entity.getFechaRevision());

        return dto;
    }

    public static Asignacion toEntity(AsignacionDTO dto) {
        Asignacion asignacion = new Asignacion();
        asignacion.setTitulo(dto.getTitulo());
        asignacion.setDescripcion(dto.getDescripcion());
        asignacion.setFechaLimite(dto.getFechaLimite());

        return asignacion;
    }

    public static AsignacionDTO toDTO(Asignacion entity) {
        AsignacionDTO dto = new AsignacionDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setFechaLimite(entity.getFechaLimite());

        if (entity.getAlumno() != null) {
            dto.setAlumnoId(entity.getAlumno().getId());
        }

        if (entity.getCurso() != null) {
            dto.setCursoId(entity.getCurso().getId());
        }

        return dto;
    }
}
