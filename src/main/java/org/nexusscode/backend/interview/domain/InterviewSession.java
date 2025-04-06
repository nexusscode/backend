package org.nexusscode.backend.interview.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.application.domain.JobApplication;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class InterviewSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    private String title;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private InterviewType interviewType;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<InterviewQuestion> questions = new ArrayList<>();

    public static InterviewSession createInterviewSession(JobApplication application, String title, List<InterviewQuestion> questions) {
        InterviewSession build = InterviewSession.builder()
                .application(application)
                .title(title)
                .startedAt(LocalDateTime.now())
                .build();

        build.questions.addAll(questions);

        return build;
    }
}