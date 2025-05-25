package org.nexusscode.backend.interview.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterviewSummaryDTO {
    private String strengths;
    private String weaknesses;
    private String overallAssessment;
    private String comparisonWithPrevious;
    private String vocabularyRepeatedWords;
    private String vocabularyLevelComment;
    private String vocabularySuggestions;
    private String workAttitude;
    private String developerStyle;
}
