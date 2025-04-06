package org.nexusscode.backend.interview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDTO;
import org.nexusscode.backend.interview.dto.InterviewStartRequest;
import org.nexusscode.backend.interview.dto.QuestionAndHintDTO;
import org.nexusscode.backend.interview.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/interview")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public ResponseEntity<Long> startInterview(@RequestBody InterviewStartRequest request) {
        return ResponseEntity.ok(interviewService.startInterview(request.getTitle(), request.getResumeId()));
    }

    @GetMapping("/{sessionId}/question")
    public ResponseEntity<QuestionAndHintDTO> getQuestion(@PathVariable Long sessionId, @RequestParam Integer seq) {
        return ResponseEntity.ok(interviewService.getQuestion(sessionId, seq));
    }

    @GetMapping("/list")
    public ResponseEntity<List<InterviewSessionDTO>> list(Long applicationId) {
        return ResponseEntity.ok(interviewService.getList());
    }
}
