package org.nexusscode.backend.interview.dto;


import lombok.Builder;
import lombok.Data;
import org.nexusscode.backend.interview.client.support.GptVoice;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class InterviewStartRequest {
    @NotNull
    private Long resumeId;
    @NotNull(message = "면접 제목을 입력해주세요.")
    private String title;
    @NotNull(message = "면접 타입을 선택해주세요.") //면접 타입없이 한번에 모두다 면접 진행할 것인지.
    private GptVoice interviewType;
    @NotNull
    private String email;
}
