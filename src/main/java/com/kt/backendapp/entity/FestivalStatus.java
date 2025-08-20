package com.kt.backendapp.entity;

import lombok.Getter;

@Getter
public enum FestivalStatus {
    BEFORE("before", "운영전"),
    DURING("during", "운영 중"),
    ENDED("ended", "운영 종료"),
    CANCELLED("cancelled", "운영 불발");

    private final String code;
    private final String displayName;

    FestivalStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    // 프론트엔드 호환성을 위한 메서드
    public static FestivalStatus fromCode(String code) {
        for (FestivalStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown festival status code: " + code);
    }
}
