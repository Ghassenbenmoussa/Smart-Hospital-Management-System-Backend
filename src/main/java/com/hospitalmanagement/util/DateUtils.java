package com.hospitalmanagement.util;

import java.time.LocalDateTime;

public final class DateUtils {

    private DateUtils() {}

    public static LocalDateTime parseIsoDateTime(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            return null;
        }
        String cleaned = dateTime;
        if (cleaned.endsWith("Z")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        int dotIndex = cleaned.indexOf('.');
        if (dotIndex > 19) {
            cleaned = cleaned.substring(0, dotIndex);
        }
        return LocalDateTime.parse(cleaned);
    }
}
