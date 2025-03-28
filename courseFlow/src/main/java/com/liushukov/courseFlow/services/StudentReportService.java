package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.ModuleProjection;
import com.liushukov.courseFlow.repositories.ModuleRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentReportService {
    private final ModuleRepository moduleRepository;

    public StudentReportService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public byte[] generateExcelReport(Long courseId) throws IOException {
        List<ModuleProjection> projections = moduleRepository.findAllModulesWithAssignmentsByCourseId(courseId);

        Map<String, Integer> assignmentMaxScores = projections.stream()
                .filter(p -> p.getAssignmentTitle() != null && p.getMaxScore() != null)
                .collect(Collectors.toMap(
                        ModuleProjection::getAssignmentTitle,
                        ModuleProjection::getMaxScore,
                        Integer::max
                ));

        Map<String, Map<String, Integer>> studentGrades = new LinkedHashMap<>();
        for (ModuleProjection proj : projections) {
            if (proj.getStudentId() == null || proj.getAssignmentTitle() == null) continue;

            String studentName = proj.getStudentFullName();
            String assignment = proj.getAssignmentTitle();
            Integer score = proj.getScore() != null ? proj.getScore() : 0;

            studentGrades.putIfAbsent(studentName, new HashMap<>());
            studentGrades.get(studentName).put(assignment, score);
        }

        int totalMaxGrade = assignmentMaxScores.values().stream().mapToInt(Integer::intValue).sum();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Course Report");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Student FullName");

            int colIdx = 1;
            for (String assignment : assignmentMaxScores.keySet()) {
                headerRow.createCell(colIdx++).setCellValue(assignment);
            }
            headerRow.createCell(colIdx++).setCellValue("Total Student Grade");
            headerRow.createCell(colIdx).setCellValue("Total Max Grade");

            int rowIdx = 1;
            for (Map.Entry<String, Map<String, Integer>> entry : studentGrades.entrySet()) {
                Row row = sheet.createRow(rowIdx++);
                String studentName = entry.getKey();
                Map<String, Integer> grades = entry.getValue();

                row.createCell(0).setCellValue(studentName);

                int totalStudentGrade = 0;
                int currentCol = 1;

                for (String assignment : assignmentMaxScores.keySet()) {
                    int score = grades.getOrDefault(assignment, 0);
                    row.createCell(currentCol++).setCellValue(score);
                    totalStudentGrade += score;
                }

                row.createCell(currentCol++).setCellValue(totalStudentGrade);
                row.createCell(currentCol).setCellValue(totalMaxGrade);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
