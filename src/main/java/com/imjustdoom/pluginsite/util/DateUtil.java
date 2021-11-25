package com.imjustdoom.pluginsite.util;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateUtil {

    public static String formatDate(int epoch, TimeZone timeZone) {
        ZonedDateTime dateTime = Instant.ofEpochMilli(epoch * 1000L)
                .atZone(timeZone.toZoneId());
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}