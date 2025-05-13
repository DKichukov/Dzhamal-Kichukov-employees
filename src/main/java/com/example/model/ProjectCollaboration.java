package com.example.model;

public record ProjectCollaboration(Integer empId1,
                                   Integer empId2,
                                   Integer projectId,
                                   long daysWorkedTogether) {
}
