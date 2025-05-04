package server.rest.api_moodle.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.rest.api_moodle.domain.ReportGradeCourseFromStudentInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResponseMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public ReportGradeCourseFromStudentInfo map(String jsonResponse) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
            List<Map<String, Object>> tables = (List<Map<String, Object>>) responseMap.get("tables");
            List<Map<String, Object>> warnings = (List<Map<String, Object>>) responseMap.get("warnings");

            if (tables != null && !tables.isEmpty()) {
                Map<String, Object> firstTable = tables.get(0);
                Integer courseId = (Integer) firstTable.get("courseid");
                String courseName = null;
                Integer userId = (Integer) firstTable.get("userid");
                String userFullname = (String) firstTable.get("userfullname");
                List<Map<String, Object>> tableData = (List<Map<String, Object>>) firstTable.get("tabledata");

                Double finalGrade = null;
                Double finalGradeMax = null;
                String finalGradePercentage = null;
                List<ReportGradeCourseFromStudentInfo.GradeItem> gradeItems = new ArrayList<>();

                // Extraer el nombre del curso
                for (Map<String, Object> rowData : tableData) {
                    if (rowData.containsKey("itemname")) {
                        Map<String, Object> itemName = (Map<String, Object>) rowData.get("itemname");
                        String className = (String) itemName.get("class");
                        String content = (String) itemName.get("content");
                        if (className != null && className.contains("category") && content != null) {
                            Pattern pattern = Pattern.compile("<span>([^<]*)</span>");
                            Matcher matcher = pattern.matcher(content);
                            if (matcher.find()) {
                                courseName = matcher.group(1).trim();
                                break;
                            }
                        }
                    }
                }

                for (Map<String, Object> rowData : tableData) {
                    if (rowData.containsKey("itemname")) {
                        Map<String, Object> itemName = (Map<String, Object>) rowData.get("itemname");
                        String className = (String) itemName.get("class");
                        String itemNameHtml = (String) itemName.get("content");
                        String simpleItemName = null;

                        Pattern pattern = Pattern.compile("<a[^>]*>([^<]*)</a>");
                        Matcher matcher = pattern.matcher(itemNameHtml);
                        if (matcher.find()) {
                            simpleItemName = matcher.group(1).trim();
                        } else {
                            pattern = Pattern.compile("<span[^>]*>([^<]*)</span>");
                            matcher = pattern.matcher(itemNameHtml);
                            if (matcher.find()) {
                                simpleItemName = matcher.group(1).trim();
                            } else {
                                simpleItemName = itemNameHtml;
                            }
                        }

                        if (className != null && className.contains("baggt")) {
                            if (rowData.containsKey("grade") && rowData.get("grade") instanceof Map) {
                                finalGrade = parseDouble(((Map<String, Object>) rowData.get("grade")).get("content"));
                            }
                            if (rowData.containsKey("range") && rowData.get("range") instanceof Map) {
                                String rangeContent = (String) ((Map<String, Object>) rowData.get("range")).get("content");
                                if (rangeContent != null && rangeContent.contains("&ndash;")) {
                                    String[] parts = rangeContent.split("&ndash;");
                                    if (parts.length > 1) {
                                        finalGradeMax = parseDouble(parts[1]);
                                    }
                                }
                            }
                            if (rowData.containsKey("percentage") && rowData.get("percentage") instanceof Map) {
                                finalGradePercentage = (String) ((Map<String, Object>) rowData.get("percentage")).get("content");
                            }
                        } else if (className != null && className.contains("level2 item")) {
                            ReportGradeCourseFromStudentInfo.GradeItem gradeItem = new ReportGradeCourseFromStudentInfo.GradeItem(
                                    simpleItemName,
                                    (rowData.containsKey("grade") && rowData.get("grade") instanceof Map) ? parseDouble(((Map<String, Object>) rowData.get("grade")).get("content")) : null,
                                    (rowData.containsKey("percentage") && rowData.get("percentage") instanceof Map) ? (String) ((Map<String, Object>) rowData.get("percentage")).get("content") : null
                            );
                            gradeItems.add(gradeItem);
                        }
                    }
                }

                List<ReportGradeCourseFromStudentInfo.Warning> apiWarnings = new ArrayList<>();
                if (warnings != null) {
                    for (Map<String, Object> warning : warnings) {
                        apiWarnings.add(new ReportGradeCourseFromStudentInfo.Warning(
                                (String) warning.get("item"),
                                (String) warning.get("itemid"),
                                (String) warning.get("warningcode"),
                                (String) warning.get("message")
                        ));
                    }
                }

                return new ReportGradeCourseFromStudentInfo(
                        courseId,
                        courseName,
                        userId,
                        userFullname,
                        finalGrade,
                        finalGradeMax,
                        finalGradePercentage,
                        gradeItems,
                        apiWarnings
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private Double parseDouble(Object value) {
        if (value instanceof String) {
            try {
                return Double.parseDouble(((String) value).replace("%", "").trim());
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }
}