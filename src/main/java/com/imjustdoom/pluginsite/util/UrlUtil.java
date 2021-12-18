package com.imjustdoom.pluginsite.util;

import com.imjustdoom.pluginsite.PluginSiteApplication;

import java.util.regex.Pattern;

public class UrlUtil {
    private static final String domain = (PluginSiteApplication.config.domain.endsWith("/")) ? PluginSiteApplication.config.domain.substring(0, PluginSiteApplication.config.domain.length() - 1) : PluginSiteApplication.config.domain;
    private static final String domain_redirect = domain + "/redirect?url=http";

    public static String encode(String text) {
        String url = text.replaceAll("/", "%2F");
        url = url.replaceAll("http", domain_redirect);
        return url;
    }

    public static String decode(String text) {
        String url = text.replaceAll(Pattern.quote(domain_redirect), "http");
        url = url.replaceAll("%2F", "/");
        return url;
    }

    public static String e(String text) {
        return encode(text);
    }

    public static String d(String text) {
        return decode(text);
    }
}
