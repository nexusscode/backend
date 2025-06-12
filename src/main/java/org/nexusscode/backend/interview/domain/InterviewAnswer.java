package org.nexusscode.backend.interview.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.interview.dto.InterviewAnswerRequest;

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
    @OneToOne(mappedBy = "answer", fetch = FetchType.LAZY)
    private AnswerFeedback answerFeedback;

    private String audioUrl;

    private int audioLength;

    @Column(nullable = false)
    private boolean cheated;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnswerStatus answerStatus = AnswerStatus.PENDING;

    public void saveScriptAndAudioLengthAndStatus(String script, int audioLength, AnswerStatus answerStatus) {
        this.transcript = script;
        this.audioLength = audioLength;
        this.answerStatus = answerStatus;
    }

    public static InterviewAnswer createInterviewAnswer(InterviewAnswerRequest request, InterviewQuestion question) {
        InterviewAnswer answer = InterviewAnswer.builder()
                .question(question)
                .audioUrl(request.getAudioUrl())
                .cheated(request.getIsCheated())
                .answerStatus(AnswerStatus.PENDING)
                .build();

        question.saveAnswer(answer);

        return answer;
    }

    public static InterviewAnswer createInterviewPassedAnswer(InterviewQuestion question) {
        InterviewAnswer answer = InterviewAnswer.builder()
                .question(question)
                .audioUrl("no link")
                .cheated(false)
                .answerStatus(AnswerStatus.PASS)
                .build();

        question.saveAnswer(answer);

        return answer;
    }

    public void changeAnswerStatus(AnswerStatus answerStatus) {
        this.answerStatus = answerStatus;
    }
}
