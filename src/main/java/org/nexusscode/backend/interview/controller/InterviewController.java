package org.nexusscode.backend.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.interview.dto.*;
import org.nexusscode.backend.interview.event.InterviewSummaryNotifier;
import org.nexusscode.backend.interview.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@Tag(name = "Interview API", description = "인터뷰 관련 API")
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewSummaryNotifier notifier;


    @Operation(summary = "면접 생성")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping("/start")
    public ResponseEntity<CommonResponse<Long>> startInterview(@RequestBody InterviewStartRequest request, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션이 시작되었습니다.", 200, interviewService.startInterview(request, userId)));
    }

    @Operation(summary = "면접 생성시 생성된 sessionId를 통한 단일 질문 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/{sessionId}/question")
    public ResponseEntity<CommonResponse<QuestionAndHintDTO>> getQuestion(
            @PathVariable Long sessionId, @RequestParam Integer seq, @RequestHeader Long userId) {
        QuestionAndHintDTO result = interviewService.getQuestion(sessionId, seq);

        return ResponseEntity.ok(new CommonResponse<>("질문 조회 성공", 200, result));
    }

    @Operation(summary = "하나의 applicationId에 대한 면접 리스트 전체 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/list")
    public ResponseEntity<CommonResponse<List<InterviewSessionDTO>>> list(@RequestParam Long applicationId, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 리스트 조회 성공", 200, interviewService.getList(applicationId)));
    }

    @Operation(summary = "답변 저장")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping("/interview-answer")
    public ResponseEntity<CommonResponse<Long>> submitAnswer(@RequestBody InterviewAnswerRequest request, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("답변 저장 완료", 200, interviewService.submitAnswer(request, userId)));
    }

    @Operation(summary = "답변 패스")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping("/interview-answer-pass")
    public ResponseEntity<CommonResponse<Long>> passAnswer(@RequestBody InterviewAnswerRequest request, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("답변 패스 완료", 200, interviewService.passAnswer(request.getQuestionId(), userId)));
    }

    @Operation(summary = "사용자 목소리를 접근 위한 s3 접근 권한 presign")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/voice-presign")
    public ResponseEntity<CommonResponse<Map<String, String>>> getUploadPresignUrlPath(@RequestParam String fileName, @RequestHeader Long userId) {
        Map<String, String> url = Map.of("url", interviewService.getUserVoicePreSignUrl(fileName));
        return ResponseEntity.ok(new CommonResponse<>("Presigned URL 생성 완료", 200, url));
    }

    @Operation(summary = "사용자 목소리를 업로드 시키기 위한 s3 접근 권한 presign")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/voice-putpresign")
    public ResponseEntity<CommonResponse<Map<String, String>>> getUploadPutPresignUrlPath(@RequestParam String fileName, @RequestHeader Long userId) {
        Map<String, String> url = Map.of("url", interviewService.getUserVoicePutPreSignUrl(fileName));
        return ResponseEntity.ok(new CommonResponse<>("Presigned URL 생성 완료", 200, url));
    }

    @Operation(summary = "tts 파일 s3 접근 권한 presign")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/tts-presign")
    public ResponseEntity<CommonResponse<Map<String, String>>> getAccessPresignUrlPath(@RequestParam String fileName, @RequestHeader Long userId) {
        Map<String, String> url = Map.of("url", interviewService.getAIVoicePreSignUrl(fileName));
        return ResponseEntity.ok(new CommonResponse<>("Presigned URL 생성 완료", 200, url));
    }

    @Operation(summary = "AI 영상 파일 s3 접근 권한 presign")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/ai-presign")
    public ResponseEntity<CommonResponse<Map<String, String>>> getAccessAIPresignUrlPath(@RequestParam String fileName, @RequestHeader Long userId) {
        Map<String, String> url = Map.of("url", interviewService.getAIVideoPreSignUrl(fileName));
        return ResponseEntity.ok(new CommonResponse<>("Presigned URL 생성 완료", 200, url));
    }

    @Operation(summary = "면접 세션에 대한 모든 정보 조회(질문, 답변, 결과지 포함)")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/{sessionId}/detail")
    public ResponseEntity<CommonResponse<InterviewAllSessionDTO>> getFullSessionDetails(@PathVariable Long sessionId, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 전체 조회 성공", 200, interviewService.getFullSessionDetail(sessionId)));
    }

    @Operation(summary = "면접 세션 삭제")
    @PreAuthorize("#userId == principal.userId")
    @DeleteMapping("/{sessionId}/delete")
    public ResponseEntity<CommonResponse<Boolean>> deleteSession(@PathVariable Long sessionId, @RequestParam Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 삭제", 200, interviewService.deleteSession(sessionId)));
    }

    @Operation(summary = "면접 요약 후처리를 위한 sse 커넥션")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/subscribe/{sessionId}")
    public ResponseEntity<CommonResponse<SseEmitter>> subscribe(@PathVariable Long sessionId, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("sse 커넥션", 200, notifier.register(sessionId)));
    }

    @Operation(summary = "최근 면접 세션 부여를 위한 메서드")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/{application}/connection")
    public ResponseEntity<CommonResponse<InterviewRecentSessionDTO>> getConnection(@PathVariable Long applicationId, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("최근 면접 세션 재연결", 200, interviewService.getRecentSession(applicationId)));
    }

    @Operation(summary = "가장 최근 면접 호출")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/recent")
    public ResponseEntity<CommonResponse<InterviewSessionDTO>> getRecentInterviewSession(@RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("가장 최근에 본 면접 세션을 호출하였습니다.", 200, interviewService.getRecentInterviewSessionByUserId(userId)));
    }

    @Operation(summary = "여태까지 호출한 횟수 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/total")
    public ResponseEntity<CommonResponse<Integer>> getTotalInterviewCall(@RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 횟수 조회", 200, interviewService.getInterviewCallCount(userId)));
    }

    @Operation(summary = "api 횟수 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/interview/rate-limit")
    public ResponseEntity<CommonResponse<RateLimitStatusDTO>> getRateLimitStatus() {
        return ResponseEntity.ok(new CommonResponse<>("api 조회 성공", 200, interviewService.getInterviewRateLimitStatus()));
    }
}

