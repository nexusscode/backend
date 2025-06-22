package org.nexusscode.backend.applicationReportMemo.dto;

import lombok.Getter;

@Getter
public class ReportMemoSavedResponseDto {
    private Long reportMemoId;
    private String companyName;

    public ReportMemoSavedResponseDto(Long reportMemoId, String companyName) {
        this.reportMemoId = reportMemoId;
        this.companyName = companyName;
    }
}
