package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class InterviewAnswerRequest {
    @NotNull
    private Long questionId;
    @NotNull(message = "오디오 url이 입력되지 않았습니다.")
    private String audioUrl;
    @NotNull(message = "컨닝 의심 결과를 입력하지 않았습니다.")
    private Boolean isCheated;  //프론트에서 텐서플로우로 컨닝의심 결과 컬럼
    private String email;
}
