package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.nexusscode.backend.interview.domain.InterviewType;

@Data
@Builder
@ToString
public class QuestionAndHintDTO {
    private Long interviewQuestionId;
    private String questionText;
    private String intentText;
    private Integer seq;
    private InterviewType type;
    private String ttsUrl;
    private String videoUrl;
    private Integer countAll;
}
