package org.nexusscode.backend.interview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.interview.client.AwsSTTClient;
import org.nexusscode.backend.interview.dto.*;
import org.nexusscode.backend.interview.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/interview_answer")
    public ResponseEntity<Long> submitAnswer(@RequestBody InterviewAnswerRequest request) {
        Long id = interviewService.submitAnswer(request.getQuestionId(), request.getAudioUrl());

        return ResponseEntity.ok(id);
    }

    @GetMapping("/presign")
    public ResponseEntity<Map<String, String>> getUploadPresignUrlPath(@RequestParam String fileName) {
        return ResponseEntity.ok(Map.of("url", AwsSTTClient.generatePresignedUrl(fileName, Duration.ofSeconds(90))));
    }


}
