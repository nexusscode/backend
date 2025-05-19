package org.nexusscode.backend.survey.dto;

import lombok.Getter;
import org.nexusscode.backend.survey.domain.SurveyResult;

@Getter
public class DevSurveyResponseDto {
    private Long id;
    private int developmentApproachScore;
    private int teamCollaborationScore;
    private int problemSolvingScore;
    private int developmentValuesScore;

    public DevSurveyResponseDto(SurveyResult surveyResult) {
        this.id=surveyResult.getId();
        this.developmentApproachScore=surveyResult.getDevelopmentApproachScore();
        this.teamCollaborationScore=surveyResult.getTeamCollaborationScore();
        this.problemSolvingScore=surveyResult.getProblemSolvingScore();
        this.developmentValuesScore=surveyResult.getDevelopmentValuesScore();
    }

}
