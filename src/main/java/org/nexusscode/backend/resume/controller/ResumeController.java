package org.nexusscode.backend.resume.controller;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.resume.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resume")
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping("/{applicationId}")
    public ResponseEntity<String> createResume(@PathVariable(name = "applicationId")Long applicationId, @RequestBody String title){

    }
}
