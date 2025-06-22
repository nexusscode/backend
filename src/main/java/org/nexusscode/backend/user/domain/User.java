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
}
