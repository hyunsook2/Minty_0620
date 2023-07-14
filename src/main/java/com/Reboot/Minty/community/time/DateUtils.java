package com.Reboot.Minty.community.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    private static final String DATE_FORMAT = "yy-MM-dd";

    public static String formatTimestamp(LocalDateTime createdDate) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createdDate, now);

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return createdDate.format(DateTimeFormatter.ofPattern("yy-MM-dd")) + " " + minutes + "분 전";
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return createdDate.format(DateTimeFormatter.ofPattern("yy-MM-dd")) + " " + hours + "시간 전";
        } else {
            return createdDate.format(DateTimeFormatter.ofPattern("yy-MM-dd"));
        }
    }
}