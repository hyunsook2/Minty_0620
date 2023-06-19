package com.Reboot.Minty.review.time;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static String formatReviewDate(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return hours + "시간 전";
        } else if (minutes < 43200) {
            long days = minutes / 1440;
            return days + "일 전";
        } else {
            return createdAt.getMonthValue() + "월 " + createdAt.getDayOfMonth() + "일";
        }
    }
    public class Main {
        public static void main(String[] args) {
            LocalDateTime createdAt = LocalDateTime.of(2023, 6, 10, 12, 0);
            String formattedDate = DateUtils.formatReviewDate(createdAt);
            System.out.println(formattedDate);
        }
    }
}