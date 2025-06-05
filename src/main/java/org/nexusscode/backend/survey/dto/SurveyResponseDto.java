package org.nexusscode.backend.survey.dto;

import java.util.List;
import lombok.Getter;
import org.nexusscode.backend.survey.domain.SurveyResult;

@Getter
public class SurveyResponseDto {
    private Long id;
    private int dominanceScore;
    private int influenceScore;
    private int steadinessScore;
    private int conscientiousnessScore;
    private String discType;
    private String discDescription;
    private List<String> discKeywords;
    private int developmentApproachScore;
    private int teamCollaborationScore;
    private int problemSolvingScore;
    private int developmentValuesScore;
    private String devType;
    private String decDescription;
    private List<String> devKeywords;

    public SurveyResponseDto(SurveyResult surveyResult) {
        this.id=surveyResult.getId();
        this.dominanceScore=surveyResult.getDominanceScore();
        this.influenceScore=surveyResult.getInfluenceScore();
        this.steadinessScore=surveyResult.getSteadinessScore();
        this.conscientiousnessScore=surveyResult.getConscientiousnessScore();
        this.discType=surveyResult.getDiscType().getName();
        this.discDescription=surveyResult.getDiscType().getDescription();
        this.discKeywords=surveyResult.getDiscType().getKeywords();
        this.developmentApproachScore=surveyResult.getDevelopmentApproachScore();
        this.teamCollaborationScore=surveyResult.getTeamCollaborationScore();
        this.problemSolvingScore=surveyResult.getProblemSolvingScore();
        this.developmentValuesScore=surveyResult.getDevelopmentValuesScore();
        this.devType=surveyResult.getDeveloperType().getName();
        this.decDescription=surveyResult.getDeveloperType().getDescription();
        this.devKeywords=surveyResult.getDeveloperType().getKeywords();
    }
}
