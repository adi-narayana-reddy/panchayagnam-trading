package com.py.utils;

import com.py.common.enums.BrokerType;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NextTriggerTimeUtil {

    // Angel One: 12:10 AM — just after midnight expiry
    private static final LocalTime ANGEL_ONE_TRIGGER_TIME = LocalTime.of(0, 10);

    // Motilal: 6:10 AM — just after 6 AM expiry
    private static final LocalTime MOTILAL_TRIGGER_TIME = LocalTime.of(6, 10);

    public static LocalDateTime computeNextTrigger(BrokerType brokerType) {
        LocalDateTime now = LocalDateTime.now();

        switch (brokerType) {
            case ANGELONE -> {
                // 12:10 AM is next day (after midnight)
                LocalDateTime nextTrigger = now.toLocalDate().plusDays(1).atTime(ANGEL_ONE_TRIGGER_TIME);
                return nextTrigger;
            }
            case MOTILAL -> {
                // today at 06:10 — if already past, push to tomorrow
                LocalDateTime todayTrigger = now.toLocalDate().atTime(MOTILAL_TRIGGER_TIME);
                return now.isBefore(todayTrigger) ? todayTrigger : todayTrigger.plusDays(1);
            }
            default -> throw new IllegalArgumentException("No trigger time defined for: " + brokerType);
        }
    }
}