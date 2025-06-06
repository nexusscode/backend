package org.nexusscode.backend.applicationReportMemo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportMemoInputSetRequest {

    private List<MemoInput> memoList;

    @Getter
    @Setter
    public static class MemoInput {
        private String question;
        private String answer;
    }
}
