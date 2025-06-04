package org.nexusscode.backend.survey.domain;

public enum DiscEnum {
    D("주도형(D)"),
    I("사교형(I)"),
    S("안정형(S)"),
    C("신중형(C)");

    private final String name;

    DiscEnum(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }
}
