package com.imjustdoom.pluginsite.util;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateUtil {

    /**
     * Format epoch timestamp to users timezone
     *
     * @param epoch    - Epoch timestamp
     * @param timeZone - Users timezone
     * @return - Returns the date and time in the users timezone
     */
    public static String formatDate(int epoch, TimeZone timeZone) {
        ZonedDateTime dateTime = Instant.ofEpochMilli(epoch * 1000L)
                .atZone(timeZone.toZoneId());
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}