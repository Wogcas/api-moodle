package server.rest.api_moodle.dtos;

import java.util.List;

public class CursoDTO {

    private int id;
    private String nombre;
    private List<Integer> alumnosIds;
    private List<Integer> asignacionesIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Integer> getAlumnosIds() {
        return alumnosIds;
    }

    public void setAlumnosIds(List<Integer> alumnosIds) {
        this.alumnosIds = alumnosIds;
    }

    public List<Integer> getAsignacionesIds() {
        return asignacionesIds;
    }

    public void setAsignacionesIds(List<Integer> asignacionesIds) {
        this.asignacionesIds = asignacionesIds;
    }
}
