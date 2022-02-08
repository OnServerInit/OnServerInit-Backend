package com.imjustdoom.pluginsite.util;

import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ssa");

    public static DateTimeFormatter getDateFormatter() {
        return format;
    }
}
