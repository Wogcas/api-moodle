package server.rest.api_moodle.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "maestros")
public class Maestro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String email;
    private String password;

    @ManyToMany
    @JoinTable(
            name = "maestro_curso",
            joinColumns = @JoinColumn(name = "maestro_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<Curso> cursos;
}
