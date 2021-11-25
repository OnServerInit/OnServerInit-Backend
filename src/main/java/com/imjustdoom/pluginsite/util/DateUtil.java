package com.imjustdoom.pluginsite.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static String formatDate(int epoch, TimeZone timeZone) {
        ZonedDateTime dateTime = Instant.ofEpochMilli(epoch * 1000L)
                .atZone(timeZone.toZoneId());
        String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return formatted;
    }
}