package org.nexusscode.backend.interview.service.support;

public enum PromptType {
    RESUME(true),
    INTERVIEW(true),
    SUMMARY(false),
    ADVICE_TECH(false),
    ADVICE_PER(false),
    REPORT_MEMO(false);

    private final boolean needsCount;

    PromptType(boolean needsCount) {
        this.needsCount = needsCount;
    }

    public boolean needsCount() {
        return needsCount;
    }
}
