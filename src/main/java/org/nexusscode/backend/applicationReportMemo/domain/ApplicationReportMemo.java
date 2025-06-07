package org.nexusscode.backend.applicationReportMemo.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.user.domain.User;

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

    @OneToMany(mappedBy = "applicationReportMemo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportMemoInputSet> inputSetList = new ArrayList<>();

    @Builder
    public ApplicationReportMemo(User user) {
        this.user = user;
    }

    public void updateAnalysisResult(String prosAndCons, String analysisResult) {
        this.prosAndCons = prosAndCons;
        this.analysisResult = analysisResult;
    }
}

