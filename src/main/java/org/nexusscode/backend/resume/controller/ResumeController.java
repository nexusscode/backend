package org.nexusscode.backend.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resume API", description = "자소서 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/application")
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "자소서 생성")
    @PreAuthorize("userId == principal.userId")
    @PostMapping("/{applicationId}/resume")
    public ResponseEntity<CommonResponse<ResumeResponseDto>> createResume(
        @RequestHeader Long userId,@PathVariable(name = "applicationId") Long applicationId) {
        ResumeResponseDto responseDto = resumeService.createResume(userId,applicationId);
        CommonResponse<ResumeResponseDto> response = new CommonResponse<>("자소서 생성이 완료되었습니다.", 200,
            responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "특정 공고에 대한 생성된 자소서 조회")
    @PreAuthorize("userId == principal.userId")
    @GetMapping("/{applicationId}/resume")
    public ResponseEntity<CommonResponse<ResumeResponseDto>> getResume(
        @RequestHeader Long userId,@PathVariable(name = "applicationId") Long applicationId) {
        ResumeResponseDto responseDto = resumeService.getResume(userId,applicationId);
        CommonResponse<ResumeResponseDto> response = new CommonResponse<>(
            "특정 공고에 대한 자소서 조회가 완료되었습니다.", 200, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

   /* @Operation(summary = "자소서 수정")
    @PutMapping("/resume/{resumeId}")
    public ResponseEntity<CommonResponse<ResumeResponseDto>> updateResume(
        @PathVariable(name = "resumeId") Long resumeId,
        @RequestBody ResumeRequestDto resumeRequestDto) {
        ResumeResponseDto responseDto = resumeService.updateResume(resumeId, resumeRequestDto);
        CommonResponse<ResumeResponseDto> response = new CommonResponse<>("자소서 수정이 완료되었습니다.", 200,
            responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }*/

    @Operation(summary = "자소서 삭제")
    @PreAuthorize("userId == principal.userId")
    @DeleteMapping("/resume/{resumeId}")
    public ResponseEntity<CommonResponse> deleteResume(
        @RequestHeader Long userId,@PathVariable(name = "resumeId") Long resumeId) {
        resumeService.deleteResume(userId,resumeId);
        CommonResponse response = new CommonResponse<>("자소서 삭제가 완료되었습니다.", 200, "");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "자소서 보관 저장")
    @PreAuthorize("userId == principal.userId")
    @PutMapping("/resume/{resumeId}/save")
    public ResponseEntity<CommonResponse> saveResumeInArchieve(@RequestHeader Long userId,@PathVariable(name = "resumeId")Long resumeId){
        resumeService.saveResumeInArchieve(userId,resumeId);
        CommonResponse response = new CommonResponse<>("보관함에 자소서 저장이 완료되었습니다.", 200, "");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "자소서 보관 취소")
    @PreAuthorize("userId == principal.userId")
    @PutMapping("/resume/{resumeId}/unsave")
    public ResponseEntity<CommonResponse> cancelResumeFromArchieve(@RequestHeader Long userId,@PathVariable(name = "resumeId")Long resumeId){
        resumeService.cancelResumeFromArchieve(userId,resumeId);
        CommonResponse response = new CommonResponse<>("보관함에 자소서 저장이 취소되었습니다.", 200, "");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
