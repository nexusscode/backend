package org.nexusscode.backend.user.dto;

import lombok.Getter;

@Getter
public class AiCountResponsedto {
    private int resumeAiCount;
    private int interviewAiCount;
    private int totalAiCount;

    public AiCountResponsedto(int resumeAiCount, int interviewAiCount, int totalAiCount) {
        this.resumeAiCount=resumeAiCount;
        this.interviewAiCount=interviewAiCount;
        this.totalAiCount=totalAiCount;
    }
}
