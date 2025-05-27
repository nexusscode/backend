package org.nexusscode.backend.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.survey.dto.DevSurveyResponseDto;
import org.nexusscode.backend.survey.dto.DiscSurveyResponseDto;
import org.nexusscode.backend.survey.dto.SurveyRequestDto;
import org.nexusscode.backend.survey.dto.SurveyResponseDto;
import org.nexusscode.backend.survey.service.SurveyResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Survey API",description = "설문 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyResultController {
    private final SurveyResultService surveyResultService;

    @Operation(summary = "최초 설문 제출")
    @PreAuthorize("userId == principal.userId")
    @PostMapping("/submit")
    public ResponseEntity<CommonResponse> submitSurvey(@RequestHeader Long userId,@RequestBody List<SurveyRequestDto> surveyRequestDtos){
        surveyResultService.submitSurvey(userId, surveyRequestDtos);
        CommonResponse response = new CommonResponse<>("설문 제출이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "최초 설문 조회")
    @PreAuthorize("userId == principal.userId")
    @GetMapping("/result")
    public ResponseEntity<CommonResponse<SurveyResponseDto>> getSurveyResult(@RequestHeader Long userId){
        SurveyResponseDto responseDto = surveyResultService.getSurveyResult(userId);
        CommonResponse<SurveyResponseDto> response = new CommonResponse<>("설문 결과 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "DISC 설문 조회")
    @PreAuthorize("userId == principal.userId")
    @GetMapping("/disc/result")
    public ResponseEntity<CommonResponse<DiscSurveyResponseDto>> getDiscSurveyResult(@RequestHeader Long userId){
        DiscSurveyResponseDto responseDto = surveyResultService.getDiscSurveyResult(userId);
        CommonResponse<DiscSurveyResponseDto> response = new CommonResponse<>("DISC 설문 결과 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "개발자 특화 설문 조회")
    @PreAuthorize("userId == principal.userId")
    @GetMapping("/dev/result")
    public ResponseEntity<CommonResponse<DevSurveyResponseDto>> getDevSurveyResult(@RequestHeader Long userId){
        DevSurveyResponseDto responseDto = surveyResultService.getDevSurveyResult(userId);
        CommonResponse<DevSurveyResponseDto> response = new CommonResponse<>("개발자 특화 설문 결과 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "DISC 설문 수정")
    @PreAuthorize("userId == principal.userId")
    @PutMapping("/disc")
    public ResponseEntity<CommonResponse> updateDiscSurvey(@RequestHeader Long userId,@RequestBody List<SurveyRequestDto> surveyRequestDtos){
        surveyResultService.updateDiscSurvey(userId, surveyRequestDtos);
        CommonResponse response = new CommonResponse<>("disc 설문 수정이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "개발자 특화 설문 수정")
    @PreAuthorize("userId == principal.userId")
    @PutMapping("/dev")
    public ResponseEntity<CommonResponse> updateDevSurvey(@RequestHeader Long userId,@RequestBody List<SurveyRequestDto> surveyRequestDtos){
        surveyResultService.updateDevSurvey(userId, surveyRequestDtos);
        CommonResponse response = new CommonResponse<>("개발자 특화 설문 수정이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
