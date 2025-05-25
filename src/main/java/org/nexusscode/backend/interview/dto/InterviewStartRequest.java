package org.nexusscode.backend.interview.dto;


import lombok.Builder;
import lombok.Data;
import org.nexusscode.backend.interview.client.support.GptVoice;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class InterviewStartRequest {
    @NotNull
    private Long applicationId;
    @NotNull(message = "면접 타입을 선택해주세요.")
    private GptVoice interviewType;
}
