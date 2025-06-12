package org.nexusscode.backend.application.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexusscode.backend.application.dto.MemoRequestDto;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItemFeedback;
import org.nexusscode.backend.user.domain.User;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "job_applications")
public class JobApplication extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "saramin_job_id")
    private String saraminJobId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_title")
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "experience_level")
    private String experienceLevel;

    @Column(name = "job_code")
    private String jobCode;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "required_education_level")
    private String requiredEducationLevel;

    private String salary;

    private String location;

    private String memo;

    @OneToOne(mappedBy = "application",cascade = CascadeType.ALL, orphanRemoval = true)
    private Resume resume;

    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewSession> interviewSessions = new ArrayList<>();

    @Builder
    public JobApplication(User user, String saraminJobId, String companyName, String jobTitle, Status status,
        LocalDateTime expirationDate, String experienceLevel, String jobCode, String jobType,
        String requiredEducationLevel,String location, String salary) {
        this.user=user;
        this.saraminJobId = saraminJobId;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.status = status;
        this.expirationDate = expirationDate;
        this.experienceLevel = experienceLevel;
        this.jobCode = jobCode;
        this.jobType = jobType;
        this.requiredEducationLevel = requiredEducationLevel;
        this.location=location;
        this.salary=salary;
    }

    public void updateMemo(MemoRequestDto memoRequestDto) {
        this.memo=memoRequestDto.getMemo();
    }

    public void setResume(Resume resume) {
        this.resume = resume;
        if (resume != null) {
            resume.setApplication(this);
        }
    }

    public void addInterviewSession(InterviewSession interviewSession) {
        if (this.interviewSessions == null) {
            this.interviewSessions = new ArrayList<>();
        }

        this.interviewSessions.add(interviewSession);
    }
}
