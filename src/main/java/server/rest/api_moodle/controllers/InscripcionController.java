package server.rest.api_moodle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.rest.api_moodle.dtos.AlumnoDTO;
import server.rest.api_moodle.dtos.CursoDTO;
import server.rest.api_moodle.dtos.InscripcionDTO;
import server.rest.api_moodle.services.AlumnoService;
import server.rest.api_moodle.services.MaestroService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private MaestroService maestroService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/alumno-curso")
    public ResponseEntity<String> inscribirAlumnoEnCurso(@RequestBody InscripcionDTO inscripcionDTO) {
        try {
            boolean inscripcionExitosa = alumnoService.inscribirAlumnoEnCurso(
                    inscripcionDTO.getAlumnoId(),
                    inscripcionDTO.getCursoId()
            );

            if (inscripcionExitosa) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Alumno inscrito al curso correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible inscribir al alumno en el curso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la petición: " + e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/maestro-curso")
    public ResponseEntity<String> asignarMaestroACurso(@RequestBody InscripcionDTO inscripcionDTO) {
        try {
            boolean asignacionExitosa = maestroService.asignarMaestroACurso(
                    inscripcionDTO.getMaestroId(),
                    inscripcionDTO.getCursoId()
            );

            if (asignacionExitosa) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Maestro asignado al curso correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible asignar al maestro al curso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la petición: " + e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/cursos-alumno/{alumnoId}")
    public ResponseEntity<List<CursoDTO>> obtenerCursosDeAlumno(@PathVariable int alumnoId) {
        try {
            List<CursoDTO> cursos = alumnoService.obtenerCursosDeAlumno(alumnoId);
            return ResponseEntity.ok(cursos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/cursos-maestro/{maestroId}")
    public ResponseEntity<List<CursoDTO>> obtenerCursosDeMaestro(@PathVariable int maestroId) {
        try {
            List<CursoDTO> cursos = maestroService.obtenerCursosDeMaestro(maestroId);
            return ResponseEntity.ok(cursos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/alumnos-curso/{cursoId}")
    public ResponseEntity<List<AlumnoDTO>> obtenerAlumnosDeCurso(@PathVariable int cursoId) {
        try {
            List<AlumnoDTO> alumnos = alumnoService.obtenerAlumnosDeCurso(cursoId);
            return ResponseEntity.ok(alumnos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/alumno-curso")
    public ResponseEntity<String> eliminarInscripcionAlumno(@RequestBody InscripcionDTO inscripcionDTO) {
        try {
            boolean eliminacionExitosa = alumnoService.eliminarInscripcionAlumno(
                    inscripcionDTO.getAlumnoId(),
                    inscripcionDTO.getCursoId()
            );

            if (eliminacionExitosa) {
                return ResponseEntity.ok("Inscripción eliminada correctamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No fue posible eliminar la inscripción");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en la petición: " + e.getMessage());
        }
    }
}
