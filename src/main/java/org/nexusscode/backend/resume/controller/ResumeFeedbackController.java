package org.nexusscode.backend.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.dto.ResumeFeedbackResponseDto;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.TestFeedbackDto;
import org.nexusscode.backend.resume.service.ResumeFeedbackService;
import org.nexusscode.backend.resume.service.ResumeItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resume Item Feedback API", description = "자소서 피드백 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resume/feedback")
public class ResumeFeedbackController {
    private final ResumeFeedbackService resumeFeedbackService;

    @Operation(summary = "단일 자소서 항목 피드백")
    @PostMapping("/{resumeItemId}")
    public ResponseEntity<CommonResponse> updateResumeFeedback(@PathVariable(name = "resumeItemId")Long resumeItemId, @RequestBody ResumeItemRequestDto resumeItemRequestDto){
        resumeFeedbackService.updateResumeFeedback(resumeItemId,resumeItemRequestDto);
        CommonResponse response = new CommonResponse("단일 자소서 항목 피드백이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "자소서 항목 최신 피드백 조회")
    @GetMapping("/{resumeItemId}")
    public ResponseEntity<CommonResponse<ResumeFeedbackResponseDto>> getResumeItemLatestFeedback(@PathVariable(name = "resumeItemId")Long resumeItemId){
        ResumeFeedbackResponseDto resumeFeedbackResponseDto = resumeFeedbackService.getResumeItemLatestFeedback(resumeItemId);
        CommonResponse<ResumeFeedbackResponseDto> response = new CommonResponse<>("자소서 항목 최신 피드백 조회가 완료되었습니다.",200,resumeFeedbackResponseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
