package server.rest.api_moodle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.adapter.Adapter;
import server.rest.api_moodle.dtos.MaestroDTO;
import server.rest.api_moodle.entities.Maestro;
import server.rest.api_moodle.repositorys.MaestroRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaestroService {

    @Autowired
    private MaestroRepository maestroRepo;

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
}
