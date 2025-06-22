package org.nexusscode.backend.user.domain;

import java.util.Arrays;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;

public enum ExperienceLevel {
    NEW("신입"),EXPERIENCED("경력");
    private String level;

    ExperienceLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public static ExperienceLevel from(String level) {
        return Arrays.stream(values())
            .filter(l -> l.level.equalsIgnoreCase(level))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_EXPERIENCE_LEVEL));
    }
}
