package org.nexusscode.backend.survey.dto;

import lombok.Getter;
import org.nexusscode.backend.survey.domain.SurveyResult;

@Getter
public class SurveyResponseDto {
    private Long id;
    private int dominanceScore;
    private int influenceScore;
    private int steadinessScore;
    private int conscientiousnessScore;
    private String primaryType;
    private String secondaryType;
    private int developmentApproachScore;
    private int teamCollaborationScore;
    private int problemSolvingScore;
    private int developmentValuesScore;

    public SurveyResponseDto(SurveyResult surveyResult) {
        this.id=surveyResult.getId();
        this.dominanceScore=surveyResult.getDominanceScore();
        this.influenceScore=surveyResult.getInfluenceScore();
        this.steadinessScore=surveyResult.getSteadinessScore();
        this.conscientiousnessScore=surveyResult.getConscientiousnessScore();
        this.primaryType=surveyResult.getPrimaryType();
        this.secondaryType=surveyResult.getSecondaryType();
        this.developmentApproachScore=surveyResult.getDevelopmentApproachScore();
        this.teamCollaborationScore=surveyResult.getTeamCollaborationScore();
        this.problemSolvingScore=surveyResult.getProblemSolvingScore();
        this.developmentValuesScore=surveyResult.getDevelopmentValuesScore();
    }
}
