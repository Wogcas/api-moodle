package server.rest.api_moodle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.rest.api_moodle.dtos.AlumnoDTO;
import server.rest.api_moodle.services.AlumnoService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoServ;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crear-alumno")
    public ResponseEntity<String> registrarAlumno(@RequestBody AlumnoDTO alumnoDTO){
        try {
            boolean registroExito = alumnoServ.crearAlumno(alumnoDTO);
            if(registroExito){
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Alumno registrado correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible registrar el alumno");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la peticion");
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/alumnos")
    public ResponseEntity<List<AlumnoDTO>> obtenerAlumnos() {
        try {
            List<AlumnoDTO> alumnos = alumnoServ.obtenerTodosLosAlumnos();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(alumnos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

}
