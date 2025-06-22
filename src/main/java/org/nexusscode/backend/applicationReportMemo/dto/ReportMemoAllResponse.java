package org.nexusscode.backend.applicationReportMemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class ReportMemoAllResponse {
    private Long id;
    private String companyName;
    private String position;
    private String companyAtmosphere;
    private Long interviewers;
    private LocalTime startTime;
    private LocalTime finishedTime;
    private LocalDate interviewDate;
}
