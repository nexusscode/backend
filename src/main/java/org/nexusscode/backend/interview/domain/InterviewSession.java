package org.nexusscode.backend.interview.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.interview.client.support.GptVoice;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSession extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    private String title;

    private int questionCount;

    private boolean additionalQuestion;

    @Enumerated(EnumType.STRING)
    private GptVoice voice;

    // 전체 저장에 저장되었는가 체크하는 컬럼
    @Column(nullable = false)
    private boolean saved;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference("session-question")
    private List<InterviewQuestion> questions = new ArrayList<>();

    public static InterviewSession createInterviewSession(
            JobApplication application, String title, List<InterviewQuestion> questions, GptVoice interviewType
    ) {
        InterviewSession build = InterviewSession.builder()
                .application(application)
                .title(title)
                .voice(interviewType)
                .build();

        build.questions.addAll(questions);

        build.questionCount = questions.size();

        for (InterviewQuestion question : questions) {
            question.saveSession(build);
        }

        return build;
    }

    public void addQuestion(List<InterviewQuestion> questions) {
        for (InterviewQuestion question : questions) {
            this.questions.add(question);
            question.saveSession(this);
        }
        this.questionCount = this.questions.size();
        this.additionalQuestion = true;
    }

    public void saveSessionToArchive() {
        this.saved = true;
    }

    public void deleteSessionToArchive() {
        this.saved = false;
    }
}