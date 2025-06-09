package org.nexusscode.backend.applicationReportMemo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ReportMemoInputSetRequest {

    private List<MemoInput> memoList;
    private String companyAtmosphere;
    private Long interviewers;

    private String companyName;
    private String position;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    @Schema(type = "string", example = "2025.03.24")
    private LocalDate interviewDate;

    @Schema(type = "string", pattern = "HH:mm", example = "14:00")
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(type = "string", pattern = "HH:mm", example = "14:00")
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime finishedTime;

    @Getter
    @Setter
    public static class MemoInput {
        private String question;
        private String answer;
    }
}
