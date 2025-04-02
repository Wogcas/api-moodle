package server.rest.api_moodle.dtos;

public class InscripcionDTO {
    private int alumnoId;
    private int maestroId;
    private int cursoId;

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public int getMaestroId() {
        return maestroId;
    }

    public void setMaestroId(int maestroId) {
        this.maestroId = maestroId;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }
}
