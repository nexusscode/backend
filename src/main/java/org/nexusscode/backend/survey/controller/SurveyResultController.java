package org.nexusscode.backend.survey.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.survey.dto.SureveyRequestDto;
import org.nexusscode.backend.survey.service.SurveyResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyResultController {
    private final SurveyResultService surveyResultService;

    @PostMapping("/submit")
    public ResponseEntity<CommonResponse> submitSurvey(@RequestBody SureveyRequestDto sureveyRequestDto){
        surveyResultService.submitSurvey(sureveyRequestDto);
        CommonResponse response = new CommonResponse<>("설문 제출이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
