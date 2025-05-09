package org.nexusscode.backend.interview.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.interview.dto.InterviewAnswerRequest;
import org.nexusscode.backend.user.domain.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    @JsonBackReference
    private InterviewQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private String audioUrl;

    private int audioLength;

    @Column(nullable = false)
    private boolean isCheated;

    @Column(nullable = false)
    private boolean isEnd;

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
                .isCheated(request.getIsCheated())
                .build();

        question.saveAnswer(answer);

        return answer;
    }
}
