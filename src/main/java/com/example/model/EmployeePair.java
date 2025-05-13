package com.example.model;

public record EmployeePair(Integer empId1, Integer empId2, long daysWorkedTogether) {
    public EmployeePair(Integer empId1,
                        Integer empId2) {
        this(empId1, empId2, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeePair that = (EmployeePair) o;

        return (empId1.equals(that.empId1) && empId2.equals(that.empId2)) ||
                (empId1.equals(that.empId2) && empId2.equals(that.empId1));
    }

    @Override
    public int hashCode() {
        return empId1.hashCode() + empId2.hashCode();
    }
}
