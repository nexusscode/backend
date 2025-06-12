package org.nexusscode.backend.interview.domain;

public enum AnswerStatus {

    PENDING("처리 대기 중"),
    PASS("답변 패스"),
    DONE("처리 완료"),
    FAILED("처리 실패");

    private final String description;

    AnswerStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

