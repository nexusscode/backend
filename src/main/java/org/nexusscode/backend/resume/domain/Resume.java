package org.nexusscode.backend.resume.domain;

import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    private String title;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeItem> resumeItems;

    @Column(name = "is_saved")
    private boolean isSaved;

    @Builder
    public Resume(JobApplication application, String title) {
        this.application = application;
        this.title = title;
    }

    public void updateResume(String title) {
        this.title = title;
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
}

