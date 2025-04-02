package server.rest.api_moodle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.rest.api_moodle.dtos.AsignacionDTO;
import server.rest.api_moodle.dtos.CalificacionDTO;
import server.rest.api_moodle.services.AsignacionService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionServ;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crear-asignacion")
    public ResponseEntity<String> registrarAsignacion(@RequestBody AsignacionDTO asignacionDTO){
        try {
            boolean registroExito = asignacionServ.crearAsignacion(asignacionDTO);
            if(registroExito){
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Asignación registrada correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible registrar la asignación");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la peticion");
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/asignaciones")
    public ResponseEntity<List<AsignacionDTO>> obtenerAsignaciones() {
        try {
            List<AsignacionDTO> asignaciones = asignacionServ.obtenerTodasLasAsignaciones();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(asignaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<List<AsignacionDTO>> obtenerAsignacionesPorAlumno(@PathVariable int alumnoId) {
        try {
            List<AsignacionDTO> asignaciones = asignacionServ.obtenerAsignacionesPorAlumno(alumnoId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(asignaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/tarea/{tareaId}")
    public ResponseEntity<List<AsignacionDTO>> obtenerAsignacionesPorTarea(@PathVariable int tareaId) {
        try {
            List<AsignacionDTO> asignaciones = asignacionServ.obtenerAsignacionesPorTarea(tareaId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(asignaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<AsignacionDTO>> obtenerAsignacionesPorCurso(@PathVariable int cursoId) {
        try {
            List<AsignacionDTO> asignaciones = asignacionServ.obtenerAsignacionesPorCurso(cursoId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(asignaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PutMapping("/calificar/{asignacionId}")
    public ResponseEntity<String> calificarAsignacion(@PathVariable int asignacionId, @RequestBody CalificacionDTO calificacionDTO) {
        try {
            boolean calificacionExitosa = asignacionServ.calificarAsignacion(asignacionId, calificacionDTO.getNota());
            if (calificacionExitosa) {
                return ResponseEntity.ok("Asignación calificada correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible calificar la asignación");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la peticion: " + e.getMessage());
        }
    }
}
