package org.nexusscode.backend.survey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyRequestDto {
    @JsonProperty("answers")
    private Map<String,Integer> answers= new HashMap<>();

}
