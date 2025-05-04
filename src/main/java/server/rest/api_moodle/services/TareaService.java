package server.rest.api_moodle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.rest.api_moodle.moodle.Adapter;
import server.rest.api_moodle.dtos.TareaDTO;
import server.rest.api_moodle.entities.Asignacion;
import server.rest.api_moodle.entities.Tarea;
import server.rest.api_moodle.repositorys.AsignacionRepository;
import server.rest.api_moodle.repositorys.TareaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepo;

    @Autowired
    private AsignacionRepository asignacionRepo;

    public boolean enviarTarea(TareaDTO tareaDTO){
        try {
            Asignacion asignacion = asignacionRepo.findById(tareaDTO.getAsignacionId())
                    .orElseThrow(() -> new Exception("La asignacion no existe"));

            Tarea tarea = Adapter.toEntity(tareaDTO);
            tareaRepo.save(tarea);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error en el servidor", ex);
        }
    }

    public List<TareaDTO> obtenerTodasLasTareas() {
        List<Tarea> tareas = tareaRepo.findAll();
        return tareas.stream()
                .map(Adapter::toDTO)
                .collect(Collectors.toList());
    }

    public TareaDTO obtenerTareaPorId(int id) {
        try {
            Tarea tarea = tareaRepo.findById(id)
                    .orElseThrow(() -> new Exception("La tarea no existe"));
            return Adapter.toDTO(tarea);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener asignaciones de la tarea: " + e.getMessage(), e);
        }
    }

    public boolean actualizarEnvioTarea(int id, TareaDTO nuevoEnvioTarea){
        try {
            Tarea tareaExistente = tareaRepo.findById(id)
                    .orElseThrow(() -> new Exception("La tarea no existe"));

            tareaExistente.setTitulo(nuevoEnvioTarea.getTitulo());
            tareaExistente.setContenido(nuevoEnvioTarea.getContenido());
            tareaExistente.setFechaEntrega(nuevoEnvioTarea.getFechaEntrega());
            tareaRepo.save(tareaExistente);

            return true;
        } catch(Exception ex) {
            throw new RuntimeException("Error en el servidor", ex);
        }
    }



}