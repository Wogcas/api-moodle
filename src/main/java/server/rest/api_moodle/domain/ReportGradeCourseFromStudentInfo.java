package server.rest.api_moodle.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReportGradeCourseFromStudentInfo(
        Integer courseid,
        String coursename,
        Integer userid,
        String userfullname,
        Double finalgrade, // La calificación final
        Double finalgrademax, // El rango máximo de la calificación final
        String finalgradepercentage, // El porcentaje de la calificación final
        List<GradeItem> gradeItems, // Lista de ítems de calificación individuales
        List<Warning> warnings // Para manejar las advertencias de la API
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GradeItem(
            String itemName,
            Double grade,
            String percentage
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Warning(
            String item,
            String itemid,
            String warningcode,
            String message
    ) {}
}
