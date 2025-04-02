package server.rest.api_moodle.adapter;

import org.jetbrains.annotations.NotNull;
import server.rest.api_moodle.dtos.AlumnoDTO;
import server.rest.api_moodle.dtos.CursoDTO;
import server.rest.api_moodle.entities.Alumno;
import server.rest.api_moodle.entities.Curso;
import server.rest.api_moodle.entities.Asignacion;

import java.util.List;
import java.util.stream.Collectors;

public class Adapter {

    public static Curso toEntity(@NotNull CursoDTO cursoDTO) {
        Curso curso = new Curso();
        curso.setNombre(cursoDTO.getNombre());
        System.out.println("Entidad Curso a guardar: " + curso);
        return curso;
    }

    public static CursoDTO toDTO(Curso curso) {
        CursoDTO dto = new CursoDTO();
        dto.setNombre(curso.getNombre());
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
}
