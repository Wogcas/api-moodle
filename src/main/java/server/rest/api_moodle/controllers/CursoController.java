package server.rest.api_moodle.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import server.rest.api_moodle.dtos.CursoDTO;
import server.rest.api_moodle.services.CursoService;

@Controller
public class CursoController {

    private final CursoService cursoServ;

    public CursoController( CursoService cursoServ){
        this.cursoServ = cursoServ;

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crear-curso")
    public ResponseEntity<String> registrarCurso(@RequestBody CursoDTO cursoDTO){
        try {
            boolean registroExito = cursoServ.crearCurso(cursoDTO);
            if(registroExito){
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Curso registrado correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible registrar el curso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error en la peticion");
        }
    }
}
