package server.rest.api_moodle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.Adapter;
import server.rest.api_moodle.dtos.CursoDTO;
import server.rest.api_moodle.dtos.MaestroDTO;
import server.rest.api_moodle.entities.Curso;
import server.rest.api_moodle.entities.Maestro;
import server.rest.api_moodle.repositorys.CursoRepository;
import server.rest.api_moodle.repositorys.MaestroRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaestroService {

    @Autowired
    private MaestroRepository maestroRepo;

    @Autowired
    private CursoRepository cursoRepo;

    public boolean crearMaestro(MaestroDTO maestroDTO){
        try {
            boolean maestroExistente = maestroRepo.existsByEmail(maestroDTO.getEmail());
            if (maestroExistente) {
                throw new Exception("El maestro con este email ya existe en el servidor");
            }
            Maestro maestro = Adapter.toEntity(maestroDTO);
            maestroRepo.save(maestro);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error en el servidor", e);
        }
    }

    public List<MaestroDTO> obtenerTodosLosMaestros() {
        List<Maestro> maestros = maestroRepo.findAll();
        return maestros.stream()
                .map(Adapter::toDTO)
                .collect(Collectors.toList());
    }

    public boolean asignarMaestroACurso(int maestroId, int cursoId) {
        try {
            Maestro maestro = maestroRepo.findById(maestroId)
                    .orElseThrow(() -> new Exception("El maestro no existe"));

            Curso curso = cursoRepo.findById(cursoId)
                    .orElseThrow(() -> new Exception("El curso no existe"));

            if (maestro.getCursos() != null && maestro.getCursos().contains(curso)) {
                return false; // Ya está asignado
            }

            if (maestro.getCursos() == null) {
                maestro.setCursos(new ArrayList<>());
            }

            maestro.getCursos().add(curso);
            maestroRepo.save(maestro);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al asignar maestro a curso: " + e.getMessage(), e);
        }
    }

    public List<CursoDTO> obtenerCursosDeMaestro(int maestroId) {
        try {
            Maestro maestro = maestroRepo.findById(maestroId)
                    .orElseThrow(() -> new Exception("El maestro no existe"));

            if (maestro.getCursos() != null) {
                return maestro.getCursos().stream()
                        .map(Adapter::toDTO)
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener cursos del maestro: " + e.getMessage(), e);
        }
    }

}
