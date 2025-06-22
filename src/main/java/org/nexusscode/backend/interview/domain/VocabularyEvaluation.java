package org.nexusscode.backend.interview.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class VocabularyEvaluation {
    @Column(name = "repeated_words", columnDefinition = "TEXT")
    private String repeatedWordsSummary;

    @Column(name = "vocabulary_level_comment", columnDefinition = "TEXT")
    private String levelComment;

    @Column(name = "vocabulary_improvement_suggestion", columnDefinition = "TEXT")
    private String improvementSuggestions;
}
