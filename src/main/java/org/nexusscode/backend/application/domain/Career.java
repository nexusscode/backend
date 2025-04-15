package org.nexusscode.backend.application.domain;

public enum Career {
    NEW("신입"),
    EXPERIENCED("경력");

    private final String career;

    Career(String career) {
        this.career = career;
    }

    public String getCareer() {
        return career;
    }
}
