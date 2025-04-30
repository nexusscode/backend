package org.nexusscode.backend.application.domain;

public enum Status {
    IN_PROGRESS("진행"),
    SUBMITTED("제출"),
    PASSED("통과"),
    FAILED("실패");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
