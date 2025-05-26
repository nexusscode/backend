package org.nexusscode.backend.resume.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.global.Timestamped;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "resumes")
public class Resume extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id")
    private JobApplication application;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeItem> resumeItems;

    @Column(name = "is_saved")
    private boolean isSaved;

    @Column(name = "feedback_status")
    private ResumeFeedbackStatus feedbackStatus;

    @Column(name = "ai_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long aiCount=0L;

    @Builder
    public Resume(JobApplication application) {
        this.application = application;
        this.isSaved=false;
        this.feedbackStatus=ResumeFeedbackStatus.BEFORE_FEEDBACK;
    }

    public void addResumeItem(ResumeItem resumeItem) {
        if (this.resumeItems == null) {
            this.resumeItems = new ArrayList<>();
        }

        this.resumeItems.add(resumeItem);
    }

    public void updateSaveStatus(boolean status) {
        this.isSaved=status;
    }

    public void updateFeedbackStatus() {
        this.feedbackStatus=ResumeFeedbackStatus.AFTER_FEEDBACK;
    }

    public void updateAiCount() {
        this.aiCount++;
    }

    public void touch() {
        this.isSaved = this.isSaved;
    }
}

