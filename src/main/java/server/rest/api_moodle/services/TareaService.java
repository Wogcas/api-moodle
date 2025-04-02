package server.rest.api_moodle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.Adapter;
import server.rest.api_moodle.dtos.TareaDTO;
import server.rest.api_moodle.entities.Curso;
import server.rest.api_moodle.entities.Tarea;
import server.rest.api_moodle.repositorys.CursoRepository;
import server.rest.api_moodle.repositorys.TareaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepo;

    @Autowired
    private CursoRepository cursoRepo;

    public boolean crearTarea(TareaDTO tareaDTO){
        try {
            Curso curso = cursoRepo.findById(tareaDTO.getCursoId())
                    .orElseThrow(() -> new Exception("El curso no existe"));

            Tarea tarea = Adapter.toEntity(tareaDTO);
            tarea.setCurso(curso);
            tareaRepo.save(tarea);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error en el servidor", e);
        }
    }

    public List<TareaDTO> obtenerTodasLasTareas() {
        List<Tarea> tareas = tareaRepo.findAll();
        return tareas.stream()
                .map(Adapter::toDTO)
                .collect(Collectors.toList());
    }
}