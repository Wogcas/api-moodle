package server.rest.api_moodle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.rest.api_moodle.dtos.MaestroDTO;
import server.rest.api_moodle.services.MaestroService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/maestros")
public class MaestroController {

    @Autowired
    private MaestroService maestroServ;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crear-maestro")
    public ResponseEntity<String> registrarMaestro(@RequestBody MaestroDTO maestroDTO){
        try {
            boolean registroExito = maestroServ.crearMaestro(maestroDTO);
            if(registroExito){
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Maestro registrado correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible registrar el maestro");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la peticion");
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/maestros")
    public ResponseEntity<List<MaestroDTO>> obtenerMaestros() {
        try {
            List<MaestroDTO> maestros = maestroServ.obtenerTodosLosMaestros();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(maestros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}
