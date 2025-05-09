package org.nexusscode.backend.interview.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private InterviewAnswer answer;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    @Column(columnDefinition = "TEXT")
    private String blindKeywords;

    public static AnswerFeedback createAnswerFeedback(InterviewAnswer interviewAnswer, String feedbackText, String blindKeywords) {
        AnswerFeedback result = AnswerFeedback.builder()
                .answer(interviewAnswer)
                .feedbackText(feedbackText)
                .blindKeywords(blindKeywords)
                .build();

        return result;
    }

}

