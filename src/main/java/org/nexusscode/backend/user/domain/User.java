package org.nexusscode.backend.user.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.applicationReportMemo.domain.ApplicationReportMemo;
import org.nexusscode.backend.applicationReportMemo.domain.ReportMemoInputSet;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.InterviewSummaryStorageBox;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItemFeedback;
import org.nexusscode.backend.survey.domain.SurveyResult;
import org.nexusscode.backend.user.dto.ProfileUpdateRequestDto;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "dev_type")
    private DevType devType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Column(nullable = false)
    private boolean social;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<MemberRole> userRoleList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplications;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private SurveyResult surveyResult;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resumes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewSession> interviewSessions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewSummaryStorageBox> interviewSummaryStorageBoxes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationReportMemo> reportMemos;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportMemoInputSet> reportMemoInputSets;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStat userStat;

    @Builder
    public User(String email, String password, String name, String phoneNumber,DevType devType, ExperienceLevel experienceLevel,List<MemberRole> userRoleList, boolean isSocial) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber=phoneNumber;
        this.devType=devType;
        this.experienceLevel=experienceLevel;
        this.userRoleList = userRoleList;
        this.social = isSocial;
    }

    public void change(String password, String name) {
        this.password = password;
        this.name = name;
    }

    public void addUserRole(MemberRole memberRole) {
        userRoleList.add(memberRole);
    }

    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        this.name=profileUpdateRequestDto.getName();
        this.phoneNumber=profileUpdateRequestDto.getPhoneNumber();
        this.devType=DevType.from(profileUpdateRequestDto.getDevType());
        this.experienceLevel=ExperienceLevel.from(profileUpdateRequestDto.getExperienceLevel());
    }

    public void addApplication(JobApplication application) {
        if (this.jobApplications == null) {
            this.jobApplications = new ArrayList<>();
        }

        this.jobApplications.add(application);
    }

    public void addSurveyResult(SurveyResult surveyResult) {
        this.surveyResult=surveyResult;
    }

    public void addResume(Resume resume) {
        if (this.resumes == null) {
            this.resumes = new ArrayList<>();
        }

        this.resumes.add(resume);
    }

    public void addInterviewSession(InterviewSession interviewSession) {
        if (this.interviewSessions == null) {
            this.interviewSessions = new ArrayList<>();
        }

        this.interviewSessions.add(interviewSession);
    }

    public void addInterviewSummaryStorageBox(InterviewSummaryStorageBox interviewSummaryStorageBox) {
        if (this.interviewSummaryStorageBoxes == null) {
            this.interviewSummaryStorageBoxes = new ArrayList<>();
        }

        this.interviewSummaryStorageBoxes.add(interviewSummaryStorageBox);
    }

    public void addApplicationReportMemo(ApplicationReportMemo reportMemo) {
        if (this.reportMemos == null) {
            this.reportMemos = new ArrayList<>();
        }

        this.reportMemos.add(reportMemo);
    }

    public void addReportMemoInputSet(ReportMemoInputSet reportMemoInputSet) {
        if (this.reportMemoInputSets == null) {
            this.reportMemoInputSets = new ArrayList<>();
        }

        this.reportMemoInputSets.add(reportMemoInputSet);
    }

    public void addUserStat(UserStat userStat) {
        this.userStat=userStat;
    }
}
