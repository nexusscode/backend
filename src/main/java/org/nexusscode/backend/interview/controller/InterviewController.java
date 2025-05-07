package org.nexusscode.backend.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.interview.dto.*;
import org.nexusscode.backend.interview.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Tag(name = "Interview API", description = "인터뷰 관련 API")
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/interview")
public class InterviewController {

    private final InterviewService interviewService;


    @Operation(summary = "면접 생성")
    @PostMapping("/start")
    public ResponseEntity<CommonResponse<Long>> startInterview(@RequestBody InterviewStartRequest request) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션이 시작되었습니다.", 200, interviewService.startInterview(request)));
    }

    @Operation(summary = "면접 생성시 생성된 sessionId를 통한 단일 질문 조회")
    @GetMapping("/{sessionId}/question")
    public ResponseEntity<CommonResponse<QuestionAndHintDTO>> getQuestion(@PathVariable Long sessionId, @RequestParam Integer seq) {
        return ResponseEntity.ok(new CommonResponse<>("질문 조회 성공", 200, interviewService.getQuestion(sessionId, seq)));
    }

    @Operation(summary = "하나의 applicationId에 대한 면접 리스트 전체 조회")
    @GetMapping("/list")
    public ResponseEntity<CommonResponse<List<InterviewSessionDTO>>> list(@RequestParam Long applicationId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 리스트 조회 성공", 200, interviewService.getList(applicationId)));
    }

    @Operation(summary = "답변 저장")
    @PostMapping("/interview_answer")
    public ResponseEntity<CommonResponse<Long>> submitAnswer(@RequestBody InterviewAnswerRequest request) {
        return ResponseEntity.ok(new CommonResponse<>("답변 저장 완료", 200, interviewService.submitAnswer(request)));
    }

    @Operation(summary = "s3 접근 권한 presign")
    @GetMapping("/presign")
    public ResponseEntity<CommonResponse<Map<String, String>>> getUploadPresignUrlPath(@RequestParam String fileName) {
        Map<String, String> url = Map.of("url", interviewService.getPreSignUrl(fileName));
        return ResponseEntity.ok(new CommonResponse<>("Presigned URL 생성 완료", 200, url));
    }

    @Operation(summary = "면접 세션에 대한 모든 정보 조회(질문, 답변, 결과지 포함)")
    @GetMapping("/{sessionId}/detail")
    public ResponseEntity<CommonResponse<InterviewAllSessionDTO>> getFullSessionDetails(@PathVariable Long sessionId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 전체 조회 성공", 200, interviewService.getFullSessionDetail(sessionId)));
    }

    @Operation(summary = "면접 세션 보관함에 저장")
    @PutMapping("/{sessionId}/save")
    public ResponseEntity<CommonResponse<Boolean>> saveSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 보관함에 저장", 200, interviewService.saveSessionToArchive(sessionId)));
    }

    @Operation(summary = "면접 세션 보관함에서 삭제")
    @DeleteMapping("/{sessionId}/delete")
    public ResponseEntity<CommonResponse<Boolean>> deleteSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 보관함에서 삭제", 200, interviewService.deleteSessionToArchive(sessionId)));
    }
}

