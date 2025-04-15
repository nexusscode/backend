package org.nexusscode.backend.application.domain;

public enum Status {
    IN_PROGRESS("진행"),
    SUBMITTED("제출"),
    PASSED("통과"),
    FAILED("탈락");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
