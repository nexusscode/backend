package org.nexusscode.backend.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.ResumeItemResponseDto;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.service.ResumeItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Resume Item API", description = "자소서 항목 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resume")
public class ResumeItemController {

    private final ResumeItemService resumeItemService;

    @Operation(summary = "자소서 항목 생성")
    @PostMapping("/{resumeId}")
    public ResponseEntity<CommonResponse<List<ResumeItemResponseDto>>> createResumeItems(
        @PathVariable(name = "resumeId") Long resumeId,
        @RequestBody List<ResumeItemRequestDto> resumeItemRequestDtos) {
        List<ResumeItemResponseDto> responseDtos = resumeItemService.createResumeItems(resumeId,
            resumeItemRequestDtos);
        CommonResponse<List<ResumeItemResponseDto>> response = new CommonResponse<>(
            "자소서 항목 생성이 완료되었습니다.", 200, responseDtos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "자소서 전체 항목 조회")
    @GetMapping("/{resumeId}")
    public ResponseEntity<CommonResponse<List<ResumeItemResponseDto>>> getResume(
        @PathVariable(name = "resumeId") Long resumeId) {
        List<ResumeItemResponseDto> responseDtos = resumeItemService.getResume(resumeId);
        CommonResponse<List<ResumeItemResponseDto>> response = new CommonResponse<>(
            "자소서 항목 조회가 완료되었습니다.", 200, responseDtos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "자소서 항목 수정")
    @PutMapping("/item/{resumeItemId}")
    public ResponseEntity<CommonResponse<ResumeItemResponseDto>> updateResumeItem(
        @PathVariable(name = "resumeItemId") Long resumeItemId,
        @RequestBody ResumeItemRequestDto resumeItemRequestDto) {
        ResumeItemResponseDto responseDto = resumeItemService.updateResumeItem(resumeItemId,
            resumeItemRequestDto);
        CommonResponse<ResumeItemResponseDto> response = new CommonResponse<>("자소서 항목 수정이 완료되었습니다.",
            200, responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "자소서 항목 삭제")
    @DeleteMapping("/item/{resumeItemId}")
    public ResponseEntity<CommonResponse> deleteResumeItem(
        @PathVariable(name = "resumeItemId") Long resumeItemId) {
        resumeItemService.deleteResumeItem(resumeItemId);
        CommonResponse response = new CommonResponse<>("자소서 항목 삭제가 완료되었습니다.", 200, "");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{resumeId}/upload/file")
    public ResponseEntity<CommonResponse<List<ResumeItemResponseDto>>> uploadResumeFile(@PathVariable(name = "resumeId")Long resumeId,@RequestParam(name = "file")
        MultipartFile file){
        List<ResumeItemResponseDto> responseDtos = resumeItemService.uploadResumeFile(resumeId,file);
        CommonResponse<List<ResumeItemResponseDto>> response = new CommonResponse<>("자소서 파일 업로드가 완료되었습니다.",200,responseDtos);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }
}
