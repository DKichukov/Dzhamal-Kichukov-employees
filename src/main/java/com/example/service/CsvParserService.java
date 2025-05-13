package com.example.service;

import com.example.model.EmployeeProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class CsvParserService {
    private static final Logger logger = LoggerFactory.getLogger(CsvParserService.class);

    private static final DateTimeFormatter MULTI_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("M-d-yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
            .appendOptional(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .toFormatter(Locale.ENGLISH);

    public List<EmployeeProject> parseCsv(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }

        List<EmployeeProject> employeeProjects = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) continue;

                String[] values = line.split(",");
                if (values.length < 4) {
                    logger.debug("Skipping line {}: insufficient data [{}]", lineNumber, Arrays.toString(values));
                    continue;
                }

                try {
                    EmployeeProject project = createEmployeeProject(values);
                    employeeProjects.add(project);
                } catch (IllegalArgumentException e) {
                    logger.warn("Error parsing line {}: {}", lineNumber, e.getMessage());
                }
            }
        }
        if (!employeeProjects.isEmpty()) {
            logger.info("Successfully parsed {} employee project records", employeeProjects.size());
        }

        return employeeProjects;
    }

    private EmployeeProject createEmployeeProject(String[] values) {

        try {
            Integer empId = Integer.parseInt(values[0].trim());
            Integer projectId = Integer.parseInt(values[1].trim());
            LocalDate dateFrom = parseDate(values[2].trim());
            LocalDate dateTo = "NULL".equalsIgnoreCase(values[3].trim()) ?
                    null : parseDate(values[3].trim());

            return new EmployeeProject(empId, projectId, dateFrom, dateTo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid employee ID format: " + values[0].trim(), e);
        }
    }

    private LocalDate parseDate(String dateStr) {

        try {
            return LocalDate.parse(dateStr, MULTI_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Unsupported date format: " + dateStr, e);
        }
    }
}
