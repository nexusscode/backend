package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class InterviewQuestionDTO {
    String question;
    String intent;
}
