package org.nexusscode.backend.user.domain;

public enum MemberRole {
    USER("일반 사용자"), ADMIN("관리자");

    private String role;

    MemberRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
