package server.rest.api_moodle.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;


//TAREA. Se refiere a la actividad que realiza el estudiante en respuesta a su asignación
@Entity
@Table(name = "tareas")
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String titulo;
    private String contenido;
    private double nota;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaRevision;

    @ManyToOne
    @JoinColumn(name = "asignacion_id")
    private Asignacion asignacion;

    @ManyToOne
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    public Tarea(){}

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

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
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

    public Asignacion getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignacion asignacion) {
        this.asignacion = asignacion;
    }
}