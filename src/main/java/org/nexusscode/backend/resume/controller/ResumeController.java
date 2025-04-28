package org.nexusscode.backend.resume.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.ResumeItemResponseDto;
import org.nexusscode.backend.resume.dto.ResumeRequestDto;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resume")
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping("/{applicationId}")
    public ResponseEntity<CommonResponse<ResumeResponseDto>> createResume(@PathVariable(name = "applicationId")Long applicationId, @RequestBody ResumeRequestDto resumeRequestDto){
        ResumeResponseDto responseDto = resumeService.createResume(applicationId,
            resumeRequestDto);
        CommonResponse response = new CommonResponse("자소서 생성이 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<CommonResponse<ResumeResponseDto>> getAllResumes(@PathVariable(name = "applicationId")Long applicationId){
        ResumeResponseDto responseDto = resumeService.getAllResumes(applicationId);
        CommonResponse response = new CommonResponse("특정 공고에 대한 모든 자소서 목록 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<CommonResponse<List<ResumeItemResponseDto>>> getResume(@PathVariable(name = "resumeId")Long resumeId){
        ResumeItemResponseDto responseDto = resumeService.getResume(resumeId);
        CommonResponse response = new CommonResponse("단건 자소서 상세 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{resumeId}")
    public ResponseEntity<CommonResponse> updateResume(@PathVariable(name = "resumeId")Long resumeId){
        resumeService.updateResume(resumeId);
        CommonResponse response = new CommonResponse("자소서 수정이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<CommonResponse> deleteResume(@PathVariable(name = "resumeId")Long resumeId){
        resumeService.deleteResume(resumeId);
        CommonResponse response = new CommonResponse("자소서 삭제가 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
