package org.nexusscode.backend.survey.dto;

import java.util.List;
import lombok.Getter;
import org.nexusscode.backend.survey.domain.SurveyResult;

@Getter
public class DiscSurveyResponseDto {
    private Long id;
    private int dominanceScore;
    private int influenceScore;
    private int steadinessScore;
    private int conscientiousnessScore;
    private String discType;
    private String description;
    private List<String> keywords;

    public DiscSurveyResponseDto(SurveyResult surveyResult) {
        this.id=surveyResult.getId();
        this.dominanceScore=surveyResult.getDominanceScore();
        this.influenceScore=surveyResult.getInfluenceScore();
        this.steadinessScore=surveyResult.getSteadinessScore();
        this.conscientiousnessScore=surveyResult.getConscientiousnessScore();
        this.discType=surveyResult.getDiscType().getName();
        this.description=surveyResult.getDiscType().getDescription();
        this.keywords=surveyResult.getDiscType().getKeywords();
    }

}
