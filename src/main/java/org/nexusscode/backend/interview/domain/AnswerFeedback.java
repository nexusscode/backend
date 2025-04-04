package org.nexusscode.backend.interview.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private InterviewAnswer answer;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    @CreatedDate
    private LocalDateTime createdAt;
}

