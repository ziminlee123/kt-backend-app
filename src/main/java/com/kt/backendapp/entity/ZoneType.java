package com.kt.backendapp.entity;

import lombok.Getter;

@Getter
public enum ZoneType {
    MAIN_STAGE("main-stage", "메인 스테이지"),
    FOOD_COURT("food-court", "푸드코트"),
    MERCHANDISE("merchandise", "굿즈샵"),
    VIP("vip", "VIP 구역"),
    PARKING("parking", "주차장");

    private final String code;
    private final String displayName;

    ZoneType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    // 프론트엔드 호환성을 위한 메서드
    public static ZoneType fromCode(String code) {
        for (ZoneType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown zone type code: " + code);
    }
}
