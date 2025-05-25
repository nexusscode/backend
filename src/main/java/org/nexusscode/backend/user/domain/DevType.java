package org.nexusscode.backend.user.domain;

import java.util.Arrays;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;

public enum DevType {
    FRONTEND("프론트엔드"),BACKEND("백엔드"),FULLSTACK("풀스텍"),DEVOPS("데브옵스"), DATA("데이터"),AI("AI/인공지능");

    private String type;

    DevType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static DevType from(String type) {
        return Arrays.stream(values())
            .filter(d -> d.type.equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_DEV_TYPE));
    }
}
