package server.rest.api_moodle.dtos;

import java.util.List;

public class MaestroDTO {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private List<Integer> cursosIds;

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getCursosIds() {
        return cursosIds;
    }

    public void setCursosIds(List<Integer> cursosIds) {
        this.cursosIds = cursosIds;
    }

}
