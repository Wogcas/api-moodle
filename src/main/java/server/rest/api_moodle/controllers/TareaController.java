package server.rest.api_moodle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.rest.api_moodle.dtos.TareaDTO;
import server.rest.api_moodle.services.TareaService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    @Autowired
    private TareaService tareaServ;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crear-tarea")
    public ResponseEntity<String> registrarTarea(@RequestBody TareaDTO tareaDTO){
        try {
            boolean registroExito = tareaServ.crearTarea(tareaDTO);
            if(registroExito){
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Tarea registrada correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible registrar la tarea");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la peticion");
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/tareas")
    public ResponseEntity<List<TareaDTO>> obtenerTareas() {
        try {
            List<TareaDTO> tareas = tareaServ.obtenerTodasLasTareas();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(tareas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

}
