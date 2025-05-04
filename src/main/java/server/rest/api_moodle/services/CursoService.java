package server.rest.api_moodle.services;

import org.springframework.stereotype.Service;
import server.rest.api_moodle.moodle.Adapter;
import server.rest.api_moodle.dtos.CursoDTO;
import server.rest.api_moodle.entities.Curso;
import server.rest.api_moodle.repositorys.CursoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CursoService {

    private final CursoRepository cursoRepo;

    public CursoService(CursoRepository cursoRepo){
        this.cursoRepo = cursoRepo;
    }

    public boolean crearCurso(CursoDTO cursoDTO){
        try {
        boolean cursoExistente = cursoRepo.existsByNombre(cursoDTO.getNombre());
        if (cursoExistente) {
            throw new Exception("El curso ya existe en el servidor");
        }
        Curso curso = Adapter.toEntity(cursoDTO);
        cursoRepo.save(curso);
        return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error en el servidor",ex);
        }
    }

    public List<CursoDTO> obtenerTodosLosCursos() {
        List<Curso> cursos = cursoRepo.findAll();
        return cursos.stream()
                .map(Adapter::toDTO)
                .collect(Collectors.toList());
    }

}
