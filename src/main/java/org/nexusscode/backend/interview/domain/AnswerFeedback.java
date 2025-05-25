package org.nexusscode.backend.interview.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerFeedback extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private InterviewAnswer answer;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    @Column(columnDefinition = "TEXT")
    private String blindKeywords;

    private Boolean completeAnswer;

    private Boolean questionFulfilled;


    public static AnswerFeedback createAnswerFeedback(InterviewAnswer interviewAnswer, String feedbackText, String blindKeywords, boolean isCompleteAnswer, boolean isQuestionFulfilled) {
        AnswerFeedback result = AnswerFeedback.builder()
                .answer(interviewAnswer)
                .feedbackText(feedbackText)
                .completeAnswer(isCompleteAnswer)
                .questionFulfilled(isQuestionFulfilled)
                .blindKeywords(blindKeywords)
                .build();

        return result;
    }

}

