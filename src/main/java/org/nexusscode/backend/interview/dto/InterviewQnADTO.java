package org.nexusscode.backend.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewQnADTO {
    private Long questionId;
    private String questionText;
    private String transcript;
    private String feedback;
    private int second;
}
