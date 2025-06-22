package org.nexusscode.backend.applicationReportMemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReportMemoDetailResponse {

    private Long reportMemoId;
    private String companyName;
    private String position;
    private String companyAtmosphere;
    private Long interviewers;
    private LocalTime startTime;
    private LocalTime finishedTime;
    private LocalDate interviewDate;
    private String prosAndCons;
    private String analysisResult;
    private boolean isSaved;
    private List<MemoOutput> memoList;

    @Getter
    @AllArgsConstructor
    public static class MemoOutput {
        private String question;
        private String answer;
    }
}
