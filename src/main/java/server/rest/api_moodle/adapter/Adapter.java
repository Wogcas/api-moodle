package server.rest.api_moodle.adapter;

import org.jetbrains.annotations.NotNull;
import server.rest.api_moodle.dtos.CursoDTO;
import server.rest.api_moodle.entities.Curso;

public class Adapter {

    public static Curso toEntity(@NotNull CursoDTO cursoDTO) {

        Curso curso = new Curso();

        curso.setNombre(cursoDTO.getNombre());
        System.out.println("Entidad Curso a guardar: " + curso);

        return curso;
    }
}
