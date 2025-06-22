package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterviewRecentSessionDTO {
    private Long sessionId;
    private int seq;
}
