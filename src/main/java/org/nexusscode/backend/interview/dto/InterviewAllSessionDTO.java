package org.nexusscode.backend.interview.dto;

import lombok.*;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.InterviewSummary;
import org.nexusscode.backend.interview.domain.VocabularyEvaluation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InterviewAllSessionDTO {
    private Long sessionId;
    private String title;
    private int totalCount;
    private List<InterviewQnADTO> questions;
    private int countSeconds;
    private String strengths;
    private String weaknesses;
    private String overallAssessment;
    private String comparisonWithPrevious;
    private VocabularyEvaluation vocabularyEvaluation;
    private String workAttitude;
    private String developerStyle;
    private int notCompleteAnswer;
    private String blindList;


    public static InterviewAllSessionDTO createAllSessionDTO(InterviewSession session, List<InterviewQnADTO> questions, InterviewSummary summary) {
        InterviewAllSessionDTO build = InterviewAllSessionDTO.builder()
                .sessionId(session.getId())
                .title(session.getTitle())
                .totalCount(questions.size())
                .questions(questions)
                .strengths(summary.getStrengths())
                .weaknesses(summary.getWeaknesses())
                .overallAssessment(summary.getOverallAssessment())
                .comparisonWithPrevious(summary.getComparisonWithPrevious())
                .vocabularyEvaluation(summary.getVocabularyEvaluation())
                .workAttitude(summary.getWorkAttitude())
                .developerStyle(summary.getDeveloperStyle())
                .countSeconds(questions.stream().mapToInt(InterviewQnADTO::getSecond).sum())
                .notCompleteAnswer((int) questions.stream().filter(q -> !q.isCompleteAnswer()).count())
                .blindList(
                        questions.stream()
                                .filter(Objects::nonNull)
                                .map(InterviewQnADTO::getBlindKeywords)
                                .filter(Objects::nonNull)
                                .filter(s -> !s.trim().equalsIgnoreCase("없음"))
                                .flatMap(s -> Arrays.stream(s.split(",")))
                                .map(String::trim)
                                .filter(s -> !s.isBlank())
                                .distinct()
                                .collect(Collectors.joining(","))
                )
                .build();

        return build;
    }
}
