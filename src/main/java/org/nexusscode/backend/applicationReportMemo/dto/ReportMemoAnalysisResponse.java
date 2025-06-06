package org.nexusscode.backend.applicationReportMemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportMemoAnalysisResponse {
    private String prosAndCons;    // 강점/약점
    private String analysisResult; // AI 요약
}
