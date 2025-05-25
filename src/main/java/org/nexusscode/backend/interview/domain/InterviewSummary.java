package org.nexusscode.backend.interview.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.interview.dto.InterviewSummaryDTO;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSummary extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private InterviewSession session;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "overall_assessment", columnDefinition = "TEXT")
    private String overallAssessment;

    @Column(name = "comparison_with_previous", columnDefinition = "TEXT")
    private String comparisonWithPrevious;

    @Embedded
    private VocabularyEvaluation vocabularyEvaluation;

    @Column(name = "work_attitude", columnDefinition = "TEXT")
    private String workAttitude;

    @Column(name = "developer_style", columnDefinition = "TEXT")
    private String developerStyle;

    public static InterviewSummary createInterviewSummary(InterviewSession session, InterviewSummaryDTO dto) {
        return InterviewSummary.builder()
                .session(session)
                .strengths(dto.getStrengths())
                .weaknesses(dto.getWeaknesses())
                .overallAssessment(dto.getOverallAssessment())
                .comparisonWithPrevious(dto.getComparisonWithPrevious())
                .vocabularyEvaluation(
                        VocabularyEvaluation.builder()
                                .repeatedWordsSummary(dto.getVocabularyRepeatedWords())
                                .levelComment(dto.getVocabularyLevelComment())
                                .improvementSuggestions(dto.getVocabularySuggestions())
                                .build()
                )
                .workAttitude(dto.getWorkAttitude())
                .developerStyle(dto.getDeveloperStyle())
                .build();
    }
}

