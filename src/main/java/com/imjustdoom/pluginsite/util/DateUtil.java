package com.imjustdoom.pluginsite.util;

import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ssa");

    public static DateTimeFormatter getDateFormatter() {
        return FORMAT;
    }
}
