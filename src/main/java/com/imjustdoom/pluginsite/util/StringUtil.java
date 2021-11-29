package com.imjustdoom.pluginsite.util;

import java.util.regex.Pattern;

public class StringUtil {

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
