package org.nexusscode.backend.survey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SurveyRequestWrapper {

    @JsonProperty("surveys")
    private List<SurveyRequestDto> surveys;
}
