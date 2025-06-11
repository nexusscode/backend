package org.nexusscode.backend.applicationReportMemo.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ApplicationReportMemo extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = true)
    private String prosAndCons; // GPT 강점 약점

    @Column(nullable = true)
    private String analysisResult; // GPT 요약

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private LocalDate interviewDate;

    @Column(nullable = false)
    private String companyAtmosphere;

    @Column(nullable = false)
    private Long interviewers;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime finishedTime;

    @Column(name = "is_saved")
    private boolean isSaved;

    @OneToMany(mappedBy = "applicationReportMemo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportMemoInputSet> inputSetList = new ArrayList<>();

    @Builder
    public ApplicationReportMemo(User user, String companyAtmosphere, Long interviewers,
                                 LocalTime startTime, LocalTime finishedTime,
                                 String companyName, String position, LocalDate interviewDate) {
        this.user = user;
        this.companyAtmosphere = companyAtmosphere;
        this.interviewers = interviewers;
        this.startTime = startTime;
        this.finishedTime = finishedTime;
        this.companyName = companyName;
        this.position = position;
        this.interviewDate = interviewDate;
        this.isSaved=false;
    }

    public void updateAnalysisResult(String prosAndCons, String analysisResult) {
        this.prosAndCons = prosAndCons;
        this.analysisResult = analysisResult;
    }

    public void updateSaveStatus(boolean status) {
        this.isSaved=status;
    }
}

