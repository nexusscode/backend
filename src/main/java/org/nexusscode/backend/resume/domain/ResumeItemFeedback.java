package org.nexusscode.backend.resume.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "resume_item_feedbacks")
public class ResumeItemFeedback extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_item_id")
    private ResumeItem resumeItem;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    @Builder
    public ResumeItemFeedback(ResumeItem resumeItem, String feedbackText) {
        this.resumeItem = resumeItem;
        this.feedbackText = feedbackText;
    }
}
