package org.nexusscode.backend.resume.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.global.Timestamped;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Resume extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    private String title;

    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeItem> resumeItems;

    @Builder
    public Resume(JobApplication application, String title) {
        this.application = application;
        this.title = title;
    }

    public void updateResume(String title) {
        this.title=title;
    }

    public void addResumeItem(ResumeItem resumeItem) {
        if (this.resumeItems == null) {
            this.resumeItems = new ArrayList<>();
        }

        this.resumeItems.add(resumeItem);
    }
}

