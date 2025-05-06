package org.nexusscode.backend.survey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SurveyRequestDto {

    @JsonProperty("questionNo")
    private int questionNo;

    @JsonProperty("score")
    private int score;
}
