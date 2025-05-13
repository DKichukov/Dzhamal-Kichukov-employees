package com.example.service;

import com.example.model.EmployeePair;
import com.example.model.EmployeeProject;
import com.example.model.ProjectCollaboration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollaborationService {

    private static final Logger logger = LoggerFactory.getLogger(CollaborationService.class);

    public Map<EmployeePair, Long> findLongestCollaboratingPair(List<EmployeeProject> projects) {
        if (projects == null || projects.isEmpty()) {
            logger.warn("Empty or null projects list provided");
            return new HashMap<>();
        }

        List<EmployeeProject> normalizedProjects = normalizeProjectDates(projects);
        Map<Integer, List<EmployeeProject>> projectsByProjectId = groupProjectsByProjectId(normalizedProjects);

        return calculateCollaborationDays(projectsByProjectId);
    }

    public List<ProjectCollaboration> getAllCollaborations(List<EmployeeProject> projects) {
        if (projects == null || projects.isEmpty()) {
            logger.warn("Empty or null projects list provided");
            return new ArrayList<>();
        }

        List<EmployeeProject> normalizedProjects = normalizeProjectDates(projects);
        Map<Integer, List<EmployeeProject>> projectsByProjectId = groupProjectsByProjectId(normalizedProjects);

        return findCollaborations(projectsByProjectId);
    }

    private List<EmployeeProject> normalizeProjectDates(List<EmployeeProject> projects) {
        return projects.stream()
                .map(project -> project.dateTo() == null ?
                        project.withDateTo(LocalDate.now()) : project)
                .toList();
    }

    private Map<Integer, List<EmployeeProject>> groupProjectsByProjectId(List<EmployeeProject> projects) {
        return projects.stream()
                .collect(Collectors.groupingBy(EmployeeProject::projectId));
    }

    private boolean employeesOverlap(EmployeeProject ep1, EmployeeProject ep2) {
        return !ep2.dateFrom().isAfter(ep1.dateTo());
    }

    private long calculateOverlapDays(EmployeeProject ep1, EmployeeProject ep2) {
        LocalDate overlapStart = ep1.dateFrom().isAfter(ep2.dateFrom()) ?
                ep1.dateFrom() : ep2.dateFrom();

        LocalDate overlapEnd = ep1.dateTo().isBefore(ep2.dateTo()) ?
                ep1.dateTo() : ep2.dateTo();

        return ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
    }

    private EmployeePair createOrderedEmployeePair(Integer empId1, Integer empId2) {
        if (empId1.compareTo(empId2) > 0) {
            Integer temp = empId1;
            empId1 = empId2;
            empId2 = temp;
        }

        return new EmployeePair(empId1, empId2);
    }

    private Map<EmployeePair, Long> calculateCollaborationDays(Map<Integer, List<EmployeeProject>> projectsByProjectId) {
        Map<EmployeePair, Long> pairDaysMap = new HashMap<>();

        for (Map.Entry<Integer, List<EmployeeProject>> entry : projectsByProjectId.entrySet()) {
            List<EmployeeProject> projectEmployees = entry.getValue();

            if (projectEmployees.size() < 2) {
                continue;
            }

            projectEmployees.sort(Comparator.comparing(EmployeeProject::dateFrom));
            processProjectCollaborations(projectEmployees, pairDaysMap);
        }

        return pairDaysMap;
    }

    private void processProjectCollaborations(List<EmployeeProject> projectEmployees, Map<EmployeePair, Long> pairDaysMap) {
        for (int i = 0; i < projectEmployees.size(); i++) {
            for (int j = i + 1; j < projectEmployees.size(); j++) {
                EmployeeProject ep1 = projectEmployees.get(i);
                EmployeeProject ep2 = projectEmployees.get(j);

                if (employeesOverlap(ep1, ep2)) {
                    addCollaborationDays(ep1, ep2, pairDaysMap);
                }
            }
        }
    }

    private void addCollaborationDays(EmployeeProject ep1, EmployeeProject ep2, Map<EmployeePair, Long> pairDaysMap) {
        long overlapDays = calculateOverlapDays(ep1, ep2);

        if (overlapDays <= 0) {
            return;
        }

        EmployeePair pair = createOrderedEmployeePair(ep1.empId(), ep2.empId());
        pairDaysMap.merge(pair, overlapDays, Long::sum);
    }
    private List<ProjectCollaboration> findCollaborations(Map<Integer, List<EmployeeProject>> projectsByProjectId) {
        List<ProjectCollaboration> collaborations = new ArrayList<>();

        for (Map.Entry<Integer, List<EmployeeProject>> entry : projectsByProjectId.entrySet()) {
            Integer projectId = entry.getKey();
            List<EmployeeProject> projectEmployees = entry.getValue();

            if (projectEmployees.size() < 2) {
                continue;
            }

            projectEmployees.sort(Comparator.comparing(EmployeeProject::dateFrom));
            findCollaborationsForProject(projectId, projectEmployees, collaborations);
        }

        return collaborations;
    }

    private void findCollaborationsForProject(Integer projectId, List<EmployeeProject> projectEmployees,
                                              List<ProjectCollaboration> collaborations) {
        for (int i = 0; i < projectEmployees.size(); i++) {
            for (int j = i + 1; j < projectEmployees.size(); j++) {
                EmployeeProject ep1 = projectEmployees.get(i);
                EmployeeProject ep2 = projectEmployees.get(j);

                if (employeesOverlap(ep1, ep2)) {
                    ProjectCollaboration collaboration = calculateCollaboration(ep1, ep2, projectId);
                    if (collaboration != null) {
                        collaborations.add(collaboration);
                    }
                }
            }
        }
    }

    private ProjectCollaboration calculateCollaboration(EmployeeProject ep1, EmployeeProject ep2, Integer projectId) {
        long overlapDays = calculateOverlapDays(ep1, ep2);

        if (overlapDays <= 0) {
            return null;
        }

        Integer finalEmpId1 = ep1.empId();
        Integer finalEmpId2 = ep2.empId();

        if (finalEmpId1.compareTo(finalEmpId2) > 0) {
            Integer temp = finalEmpId1;
            finalEmpId1 = finalEmpId2;
            finalEmpId2 = temp;
        }

        return new ProjectCollaboration(
                finalEmpId1,
                finalEmpId2,
                projectId,
                overlapDays
        );
    }
}
