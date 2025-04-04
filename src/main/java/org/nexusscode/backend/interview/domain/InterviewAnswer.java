package org.nexusscode.backend.interview.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.user.domain.User;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private InterviewQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @CreatedDate
    private LocalDateTime createdAt;
}
