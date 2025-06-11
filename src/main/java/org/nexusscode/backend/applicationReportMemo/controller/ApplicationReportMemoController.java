package org.nexusscode.backend.applicationReportMemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.applicationReportMemo.dto.*;
import org.nexusscode.backend.applicationReportMemo.service.ApplicationReportMemoService;
import org.nexusscode.backend.applicationReportMemo.domain.ApplicationReportMemo;

import org.nexusscode.backend.global.common.CommonResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ApplicationReportMemo API", description = "강점 약점 요약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ApplicationReportMemo")
public class ApplicationReportMemoController {

    private final ApplicationReportMemoService applicationReportMemoService;

    @Operation(summary = "사용자 질문 & 답 입력")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping
    public ResponseEntity<CommonResponse<ReportMemoDetailResponse>> setUserAnswer(@RequestHeader Long userId,
                                                                                  @RequestBody ReportMemoInputSetRequest request) {
        ReportMemoDetailResponse response = applicationReportMemoService.saveUserInput(userId, request);
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

    @Operation(summary = "강점&약점 질문&답변 달일 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/{reportMemoId}")
    public ResponseEntity<CommonResponse<ReportMemoDetailResponse>> getReportMemoDetail(@RequestHeader Long userId,
                                                                                        @PathVariable Long reportMemoId
    ) {
        ReportMemoDetailResponse response = applicationReportMemoService.getMemoDetail(userId, reportMemoId);
        return ResponseEntity.ok(new CommonResponse<>("보고서 상세 조회 완료", 200, response));
    }

//    @Operation(summary = "ApplicationReportMemo 전체 조회")
//    @PreAuthorize("#userId == principal.userId")
//    @GetMapping("/all")
//    public ResponseEntity<CommonResponse<List<ReportMemoAllResponse>>> getAllMemosByUser(@RequestHeader Long userId) {
//        List<ReportMemoAllResponse> memos = applicationReportMemoService.getAllReportMemosByUserId(userId);
//        return ResponseEntity.ok(new CommonResponse<>("전체 메모 조회 완료", 200, memos));
//    }

    @Operation(summary = "실제면접 기록 보관함 저장")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping("/storage/{reportMemoId}")
    public ResponseEntity<CommonResponse> saveArchive(@RequestHeader Long userId,
                                                      @PathVariable(value = "reportMemoId") Long reportMemoId) {
        applicationReportMemoService.saveReportMemoInArchive(userId, reportMemoId);
        CommonResponse response = new CommonResponse<>("보관함에 면접 기록 저장이 완료되었습니다.", 200, "");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "실제면접 기록 보관함 취소")
    @PreAuthorize("#userId == principal.userId")
    @DeleteMapping("/storage/{reportMemoId}")
    public ResponseEntity<CommonResponse> deleteArchive(@RequestHeader Long userId,
                                                      @PathVariable(value = "reportMemoId") Long reportMemoId) {
        applicationReportMemoService.cancelReportMemoFromArchieve(userId, reportMemoId);
        CommonResponse response = new CommonResponse<>("보관함에 면접 기록이 삭제되었습니다.", 200, "");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "보관된 실제 면접 기록 전체 조회 및 검색")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/storage/saved")
    public ResponseEntity<CommonResponse<Page<ReportMemoSavedResponseDto>>> getSavedReportMemos(
            @RequestHeader Long userId,
            @RequestParam(required = false, defaultValue = "") String searchWord,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ReportMemoSavedResponseDto> responsePage =
                applicationReportMemoService.getSavedReportMemos(userId, searchWord, page-1, size);
        CommonResponse<Page<ReportMemoSavedResponseDto>> response =
                new CommonResponse<>("보관된 실제 면접 기록 전체 조회가 완료되었습니다.", 200, responsePage);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
