package org.nexusscode.backend.interview.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.interview.dto.InterviewAnswerRequest;
import org.nexusscode.backend.user.domain.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "question")
public class InterviewAnswer extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonBackReference("question-answer")
    private InterviewQuestion question;

    @JsonIgnore
    @OneToOne(mappedBy = "answer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private AnswerFeedback answerFeedback;

    private String audioUrl;

    private int audioLength;

    @Column(nullable = false)
    private boolean cheated;

    @Column(nullable = false)
    private boolean end;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    public void saveScriptAndAudioLength(String script, int audioLength) {
        this.transcript = script;
        this.audioLength = audioLength;
    }

    public static InterviewAnswer createInterviewAnswer(InterviewAnswerRequest request, InterviewQuestion question) {
        InterviewAnswer answer = InterviewAnswer.builder()
                .question(question)
                .audioUrl(request.getAudioUrl())
                .cheated(request.getIsCheated())
                .build();

        question.saveAnswer(answer);

        return answer;
    }
}
