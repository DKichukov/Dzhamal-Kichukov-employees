package com.example.controller;

import com.example.model.EmployeePair;
import com.example.model.EmployeeProject;
import com.example.model.ProjectCollaboration;
import com.example.service.CollaborationService;
import com.example.service.CsvParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private static final String ERROR = "error";
    private final CsvParserService csvParserService;
    private final CollaborationService collaborationService;

    public EmployeeController(CsvParserService csvParserService,
                              CollaborationService collaborationService) {

        this.csvParserService = csvParserService;
        this.collaborationService = collaborationService;
    }

    @GetMapping("/")
    public String index() {

        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   Model model) {

        try {
            if (file.isEmpty()) {
                model.addAttribute(ERROR, "Please select a file to upload");
                return ERROR;
            }

            logger.info("Processing uploaded file: {}", file.getOriginalFilename());

            List<EmployeeProject> projects = csvParserService.parseCsv(file);

            if (projects.isEmpty()) {
                model.addAttribute(ERROR, "No valid data found in the file");
                return ERROR;
            }

            Map<EmployeePair, Long> collaborations = collaborationService.findLongestCollaboratingPair(projects);

            if (collaborations.isEmpty()) {
                model.addAttribute("message", "No employee collaborations found");
                return "result";
            }

            Map.Entry<EmployeePair, Long> longestPair = Collections.max(
                    collaborations.entrySet(),
                    Map.Entry.comparingByValue()
            );

            List<ProjectCollaboration> allCollaborations = collaborationService.getAllCollaborations(projects);

            model.addAttribute("empId1", longestPair.getKey().empId1());
            model.addAttribute("empId2", longestPair.getKey().empId2());
            model.addAttribute("totalDays", longestPair.getValue());
            model.addAttribute("collaborations", allCollaborations);

            logger.info("Longest collaborating pair: {} and {} with {} days",
                    longestPair.getKey().empId1(),
                    longestPair.getKey().empId2(),
                    longestPair.getValue());

            return "result";
        } catch (Exception e) {
            logger.error("Error processing file", e);
            model.addAttribute(ERROR, "Error processing file: " + e.getMessage());
            return ERROR;
        }
    }
}
