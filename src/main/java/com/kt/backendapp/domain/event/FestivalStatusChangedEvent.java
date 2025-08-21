package com.kt.backendapp.domain.event;

import com.kt.backendapp.entity.FestivalStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 축제 상태 변경 이벤트
 */
@Getter
@RequiredArgsConstructor
public class FestivalStatusChangedEvent {
    
    private final Long festivalId;
    private final String festivalName;
    private final FestivalStatus previousStatus;
    private final FestivalStatus newStatus;
    private final LocalDateTime changedAt;
    private final String reason;
    
    public static FestivalStatusChangedEvent of(Long festivalId, String festivalName, 
                                               FestivalStatus previousStatus, FestivalStatus newStatus, 
                                               String reason) {
        return new FestivalStatusChangedEvent(
                festivalId, 
                festivalName, 
                previousStatus, 
                newStatus, 
                LocalDateTime.now(), 
                reason
        );
    }
}
