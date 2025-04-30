package org.nexusscode.backend.application.domain;

public enum Status {
    RESUME_IN_PROGRESS("자소서 진행"),
    RESUME_COMPLETED("자소서 완료"),
    INTERVIEW_IN_PROGRESS("면접 진행"),
    INTERVIEW_COMPLETED("면접 완료");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
