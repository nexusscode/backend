package org.nexusscode.backend.applicationReportMemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReportMemoInputSetResponse {

    private long userId;
    private String companyName;
    private String position;
    private Long interviewers;
    private LocalTime startTime;
    private LocalTime finishedTime;
    private LocalDate interviewDate;
    private String companyAtmosphere;
    private List<MemoInput> saveMemos;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MemoInput  {
        private Long id;
        private String question;
        private String answer;
    }
}
