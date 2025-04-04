package server.rest.api_moodle.adapter;

import server.rest.api_moodle.dtos.*;
import server.rest.api_moodle.entities.*;

import java.util.List;
import java.util.stream.Collectors;

public class Adapter {

    public static Curso toEntity(CursoDTO cursoDTO) {
        Curso curso = new Curso();
        curso.setNombre(cursoDTO.getNombre());
        curso.setId(curso.getId());
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

        if (entity.getTareas() != null) {
            List<Integer> tareasIds = entity.getTareas().stream()
                    .map(Tarea::getId)
                    .collect(Collectors.toList());
            dto.setTareasIds(tareasIds);
        }

        return dto;
    }

    public static Alumno toEntity(AlumnoDTO dto) {
        Alumno alumno = new Alumno();
        alumno.setId(dto.getId());
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
        maestro.setId(dto.getId());
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
        tarea.setId(dto.getId());
        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setFechaEntrega(dto.getFechaEntrega());
        tarea.setFechaRevision(dto.getFechaRevision());
        return tarea;
    }

    public static TareaDTO toDTO(Tarea entity) {
        TareaDTO dto = new TareaDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setFechaEntrega(entity.getFechaEntrega());
        dto.setFechaRevision(entity.getFechaRevision());

        if (entity.getCurso() != null) {
            dto.setCursoId(entity.getCurso().getId());
        }

        if (entity.getAsignaciones() != null) {
            List<Integer> asignacionesIds = entity.getAsignaciones().stream()
                    .map(Asignacion::getId)
                    .collect(Collectors.toList());
            dto.setAsignacionesIds(asignacionesIds);
        }

        return dto;
    }

    public static Asignacion toEntity(AsignacionDTO dto) {
        Asignacion asignacion = new Asignacion();
        asignacion.setId(dto.getId());
        asignacion.setNota(dto.getNota());
        asignacion.setFechaPublicacion(dto.getFechaPublicacion());
        return asignacion;
    }

    public static AsignacionDTO toDTO(Asignacion entity) {
        AsignacionDTO dto = new AsignacionDTO();
        dto.setId(entity.getId());
        dto.setNota(entity.getNota());
        dto.setFechaPublicacion(entity.getFechaPublicacion());

        if (entity.getAlumno() != null) {
            dto.setAlumnoId(entity.getAlumno().getId());
        }

        if (entity.getTarea() != null) {
            dto.setTareaId(entity.getTarea().getId());
        }

        return dto;
    }
}
