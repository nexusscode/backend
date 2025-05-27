package org.nexusscode.backend.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.resume.dto.ResumeItemFeedbackResponseDto;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.service.ResumeItemFeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resume Item Feedback API", description = "자소서 피드백 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resume/feedback")
public class ResumeItemFeedbackController {
    private final ResumeItemFeedbackService resumeItemFeedbackService;

    @Operation(summary = "단일 자소서 항목 재검사 피드백")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping("/{resumeItemId}")
    public ResponseEntity<CommonResponse<ResumeItemFeedbackResponseDto>> updateResumeFeedback(@RequestHeader Long userId, @PathVariable(name = "resumeItemId")Long resumeItemId, @RequestBody ResumeItemRequestDto resumeItemRequestDto){
        ResumeItemFeedbackResponseDto resumeItemFeedbackResponseDto = resumeItemFeedbackService.updateResumeItemFeedback(userId,resumeItemId,resumeItemRequestDto);
        CommonResponse response = new CommonResponse("단일 자소서 항목 재검사 피드백이 완료되었습니다.",200,resumeItemFeedbackResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "자소서 항목 최신 피드백 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/{resumeItemId}")
    public ResponseEntity<CommonResponse<ResumeItemFeedbackResponseDto>> getResumeItemLatestFeedback(@RequestHeader Long userId,@PathVariable(name = "resumeItemId")Long resumeItemId){
        ResumeItemFeedbackResponseDto resumeItemFeedbackResponseDto = resumeItemFeedbackService.getResumeItemLatestFeedback(userId,resumeItemId);
        CommonResponse<ResumeItemFeedbackResponseDto> response = new CommonResponse<>("자소서 항목 최신 피드백 조회가 완료되었습니다.",200,
            resumeItemFeedbackResponseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
