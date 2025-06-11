package org.nexusscode.backend.interview.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSummaryStorageBox extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String sessionTitle;
    private int totalQuestionCount;
    private int countSeconds;

    @Column(columnDefinition = "TEXT")
    private String strengths;
    @Column(columnDefinition = "TEXT")
    private String weaknesses;
    @Column(columnDefinition = "TEXT")
    private String overallAssessment;
    @Column(columnDefinition = "TEXT")
    private String comparisonWithPrevious;
    @Embedded
    private VocabularyEvaluation vocabularyEvaluation;
    @Column(columnDefinition = "TEXT")
    private String workAttitude;
    @Column(columnDefinition = "TEXT")
    private String developerStyle;
    private int notCompleteAnswer;
    private String blindList;

    @OneToMany(mappedBy = "storageBox", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterviewQnAStorageBox> questions = new ArrayList<>();
    
}
