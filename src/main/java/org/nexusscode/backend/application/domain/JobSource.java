package org.nexusscode.backend.application.domain;

public enum JobSource {
    SARAMIN("사람인"), WANTED("원티드");

    private final String source;

    JobSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
