package server.rest.api_moodle.dtos;

public class InscripcionDTO {
    private Integer alumnoId;
    private Integer maestroId;
    private Integer cursoId;

    public Integer getCursoId() {
        return cursoId;
    }

    public void setCursoId(Integer cursoId) {
        this.cursoId = cursoId;
    }

    public Integer getMaestroId() {
        return maestroId;
    }

    public void setMaestroId(Integer maestroId) {
        this.maestroId = maestroId;
    }

    public Integer getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Integer alumnoId) {
        this.alumnoId = alumnoId;
    }
}
