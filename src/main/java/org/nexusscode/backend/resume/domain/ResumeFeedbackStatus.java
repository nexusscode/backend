package org.nexusscode.backend.resume.domain;

public enum ResumeFeedbackStatus {
    BEFORE_FEEDBACK("검사 전"),AFTER_FEEDBACK("검사 후");

    private String status;

    ResumeFeedbackStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
