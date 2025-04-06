package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionAndHintDTO {
    private Long interviewQuestionId;
    private String questionText;
    private String intentText;
    private Integer seq;
}
