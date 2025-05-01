package org.nexusscode.backend.resume.controller;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.resume.dto.TestFeedbackDto;
import org.nexusscode.backend.resume.service.ResumeFeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resume/feedback")
public class ResumeFeedbackController {
    private final ResumeFeedbackService resumeFeedbackService;

    @PostMapping
    public ResponseEntity<CommonResponse> createResumeFeedback(@RequestBody TestFeedbackDto testFeedbackDto){
        String feedback = resumeFeedbackService.getResumeFeedback(testFeedbackDto.getQuestion(),testFeedbackDto.getAnswer());
        CommonResponse response = new CommonResponse("자소서 피드백이 완료되었습니다.",200,feedback);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
