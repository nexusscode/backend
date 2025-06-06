package org.nexusscode.backend.applicationReportMemo.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor
public class ReportMemoInputSet extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_report_memo_id")
    private ApplicationReportMemo applicationReportMemo;

    @Builder
    public ReportMemoInputSet(String question, String answer, User user, ApplicationReportMemo applicationReportMemo) {
        this.question = question;
        this.answer = answer;
        this.user = user;
        this.applicationReportMemo = applicationReportMemo;
    }
}
