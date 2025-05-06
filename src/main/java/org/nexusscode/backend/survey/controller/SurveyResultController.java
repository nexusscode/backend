package org.nexusscode.backend.survey.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.survey.dto.SurveyRequestDto;
import org.nexusscode.backend.survey.dto.SurveyRequestWrapper;
import org.nexusscode.backend.survey.dto.SurveyResponseDto;
import org.nexusscode.backend.survey.service.SurveyResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/survey")
public class SurveyResultController {
    private final SurveyResultService surveyResultService;

    @PostMapping("/submit")
    public ResponseEntity<CommonResponse> submitSurvey(@RequestBody SurveyRequestWrapper wrapper){
        System.out.println("Received wrapper: " + wrapper);
        System.out.println("Wrapper surveys field: " + wrapper.getSurveys());

        if (wrapper == null || wrapper.getSurveys() == null) {
            return new ResponseEntity<>(new CommonResponse<>("설문 응답이 비어있습니다.", 400, ""), HttpStatus.BAD_REQUEST);
        }
        List<SurveyRequestDto> surveyList = wrapper.getSurveys();
        System.out.println("설문 응답 목록: " + surveyList);
        surveyResultService.submitSurvey(surveyList);
        CommonResponse response = new CommonResponse<>("설문 제출이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/result/{resultId}")
    public ResponseEntity<CommonResponse<SurveyResponseDto>> getSurveyResult(@PathVariable(name = "resultId")Long resultId){
        SurveyResponseDto responseDto = surveyResultService.getSurveyResult(resultId);
        CommonResponse<SurveyResponseDto> response = new CommonResponse<>("설문 결과 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
