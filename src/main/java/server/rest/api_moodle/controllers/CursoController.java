package server.rest.api_moodle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.rest.api_moodle.dtos.CursoDTO;
import server.rest.api_moodle.services.CursoService;
import server.rest.api_moodle.test.MoodleTestService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private MoodleTestService moodleTestService;

    @GetMapping("/courses")
    public ResponseEntity<List<Object>> getCourses(){
        Map<String, String> params = new HashMap<>();
        List<Object> courses = moodleTestService.getCourses(params);
        return ResponseEntity.ok(courses);
    }


//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping("/crear-curso")
//    public ResponseEntity<String> registrarCurso(@RequestBody CursoDTO cursoDTO){
//        try {
//            boolean registroExito = cursoServ.crearCurso(cursoDTO);
//            if(registroExito){
//                return ResponseEntity.status(HttpStatus.CREATED)
//                        .body("Curso registrado correctamente");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("No fue posible registrar el curso");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error en la peticion");
//        }
//    }
//
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    @GetMapping("/cursos")
//    public ResponseEntity<List<CursoDTO>> obtenerCursos() {
//        try {
//            List<CursoDTO> cursos = cursoServ.obtenerTodosLosCursos();
//            return ResponseEntity.status(HttpStatus.ACCEPTED).body(cursos);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.emptyList());
//        }
//    }
}
