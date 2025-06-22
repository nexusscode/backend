package org.nexusscode.backend.interview.dto;

import lombok.*;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.InterviewSummary;
import org.nexusscode.backend.interview.domain.InterviewSummaryStorageBox;
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

    public static InterviewAllSessionDTO boxEntityToDTO(InterviewSummaryStorageBox box) {
        return InterviewAllSessionDTO.builder()
                .sessionId(box.getId())
                .title(box.getSessionTitle())
                .totalCount(box.getTotalQuestionCount())
                .questions(
                        box.getQuestions().stream()
                                .map(q -> InterviewQnADTO.builder()
                                        .questionId(q.getId())
                                        .questionText(q.getQuestionText())
                                        .transcript(q.getTranscript())
                                        .feedback(q.getFeedback())
                                        .second(q.getSecond())
                                        .cheated(q.isCheated())
                                        .completeAnswer(q.isCompleteAnswer())
                                        .questionFulfilled(q.isQuestionFulfilled())
                                        .blindKeywords(q.getBlindKeywords())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .countSeconds(box.getCountSeconds())
                .strengths(box.getStrengths())
                .weaknesses(box.getWeaknesses())
                .overallAssessment(box.getOverallAssessment())
                .comparisonWithPrevious(box.getComparisonWithPrevious())
                .vocabularyEvaluation(box.getVocabularyEvaluation())
                .workAttitude(box.getWorkAttitude())
                .developerStyle(box.getDeveloperStyle())
                .notCompleteAnswer(box.getNotCompleteAnswer())
                .blindList(box.getBlindList())
                .build();
    }
}
