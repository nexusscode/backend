package org.nexusscode.backend.survey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class SurveyRequestWrapper {

    @JsonProperty("surveys")
    private List<SurveyRequestDto> surveys;
}
