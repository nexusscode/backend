package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterviewSessionDetailDto {
    private Long sessionId;
    private String sessionTitle;
    private Long questionId;
    private String questionText;
    private Long answerId;
    private String transcript;
}
