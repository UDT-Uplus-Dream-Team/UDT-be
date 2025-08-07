package com.example.udtbe.domain.batch.util;

import java.time.LocalDateTime;

public class TimeUtil {


    public static final int SCHEDULED_HOUR = 4;
    public static final int SCHEDULED_MINUTE = 0;
    public static final int SCHEDULED_SECOND = 0;
    public static final int SCHEDULED_NANO = 0;

    public static LocalDateTime getScheduledAt() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayAtFour = now.withHour(SCHEDULED_HOUR).withMinute(SCHEDULED_MINUTE)
                .withSecond(SCHEDULED_SECOND).withNano(SCHEDULED_NANO);

        if (now.isBefore(todayAtFour)) {
            return todayAtFour;
        } else {
            return todayAtFour.plusDays(1);
        }
    }
}
