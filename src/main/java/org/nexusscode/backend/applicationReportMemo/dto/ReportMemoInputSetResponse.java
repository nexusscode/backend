package org.nexusscode.backend.applicationReportMemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReportMemoInputSetResponse {

    private long userId;
    private List<MemoOutput> saveMemos;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MemoOutput {
        private Long id;
        private String question;
        private String answer;
    }
}
