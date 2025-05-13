package com.example.model;

import java.time.LocalDate;

public record EmployeeProject(Integer empId,
                              Integer projectId,
                              LocalDate dateFrom,
                              LocalDate dateTo)
        implements Comparable<EmployeeProject> {
    @Override
    public int compareTo(EmployeeProject o) {

        int empCompare = this.empId.compareTo(o.empId());
        if (empCompare != 0) {
            return empCompare;
        }
        return this.dateFrom.compareTo(o.dateFrom());
    }
}

