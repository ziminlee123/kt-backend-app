package com.kt.backendapp.entity;

import lombok.Getter;

@Getter
public enum SentimentType {
    POSITIVE("positive", "긍정적"),
    NEUTRAL("neutral", "중립적"),
    NEGATIVE("negative", "부정적");

    private final String code;
    private final String displayName;

    SentimentType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static SentimentType fromCode(String code) {
        for (SentimentType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown sentiment type code: " + code);
    }
}
