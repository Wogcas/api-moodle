package server.rest.api_moodle.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class TareaDTO {

    private int id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaRevision;
    private int cursoId;
    private List<Integer> asignacionesIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    public List<Integer> getAsignacionesIds() {
        return asignacionesIds;
    }

    public void setAsignacionesIds(List<Integer> asignacionesIds) {
        this.asignacionesIds = asignacionesIds;
    }

}
