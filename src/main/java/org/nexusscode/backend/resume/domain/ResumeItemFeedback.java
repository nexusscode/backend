package org.nexusscode.backend.resume.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ResumeItemFeedback extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_item_id")
    private ResumeItem resumeItem;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;
}
