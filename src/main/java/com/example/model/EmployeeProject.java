package com.example.model;

import java.time.LocalDate;

public record EmployeeProject(Integer empId,
                              String projectId,
                              LocalDate dateFrom,
                              LocalDate dateTo) {
}
