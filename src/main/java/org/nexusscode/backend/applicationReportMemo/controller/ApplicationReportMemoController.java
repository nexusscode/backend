package org.nexusscode.backend.applicationReportMemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.applicationReportMemo.dto.ReportMemoAnalysisResponse;
import org.nexusscode.backend.applicationReportMemo.dto.ReportMemoInputSetRequest;
import org.nexusscode.backend.applicationReportMemo.dto.ReportMemoInputSetResponse;
import org.nexusscode.backend.applicationReportMemo.service.ApplicationReportMemoService;
import org.nexusscode.backend.global.common.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ApplicationReportMemo API", description = "강점 약점 요약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ApplicationReportMemo")
public class ApplicationReportMemoController {

    private final ApplicationReportMemoService applicationReportMemoService;

    @Operation(summary = "사용자 질문 & 답 입력")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping
    public ResponseEntity<CommonResponse<ReportMemoInputSetResponse>> setUserAnswer(@RequestHeader Long userId,
                                                                                    @RequestBody ReportMemoInputSetRequest request) {
        ReportMemoInputSetResponse response = applicationReportMemoService.saveUserInput(userId, request);
        return ResponseEntity.ok(new CommonResponse<>("메모가 생성되었습니다.", 200, response));
    }

    @Operation(summary = "GPT 강점 약점 요악 답변 받기")
    @PreAuthorize("#userId == principal.userId")
    @PutMapping("/analysis/{reportMemoId}")
    public ResponseEntity<CommonResponse<ReportMemoAnalysisResponse>> getGptAnalysisResponse(@RequestHeader Long userId,
                                                                                             @PathVariable(value = "reportMemoId") Long reportMemoId) {
        ReportMemoAnalysisResponse response = applicationReportMemoService.getAnalysisForGpt(userId, reportMemoId);
        return ResponseEntity.ok(new CommonResponse<>("GPT 답변이 생성되었습니다.", 200, response));
    }
}
