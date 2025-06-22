package org.nexusscode.backend.survey.dto;

import java.util.List;
import lombok.Getter;
import org.nexusscode.backend.survey.domain.SurveyResult;

@Getter
public class DevSurveyResponseDto {
    private Long id;
    private int developmentApproachScore;
    private int teamCollaborationScore;
    private int problemSolvingScore;
    private int developmentValuesScore;
    private String devType;
    private String description;
    private List<String> keywords;

    public DevSurveyResponseDto(SurveyResult surveyResult) {
        this.id=surveyResult.getId();
        this.developmentApproachScore=surveyResult.getDevelopmentApproachScore();
        this.teamCollaborationScore=surveyResult.getTeamCollaborationScore();
        this.problemSolvingScore=surveyResult.getProblemSolvingScore();
        this.developmentValuesScore=surveyResult.getDevelopmentValuesScore();
        this.devType=surveyResult.getDeveloperType().getName();
        this.description=surveyResult.getDeveloperType().getDescription();
        this.keywords=surveyResult.getDeveloperType().getKeywords();
    }

}
