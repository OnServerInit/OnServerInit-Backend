package com.imjustdoom.pluginsite.util;

import com.imjustdoom.pluginsite.config.custom.SiteConfig;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UrlUtil {
    private final String domainRedirect;

    public UrlUtil(SiteConfig siteConfig) {
        String domain = siteConfig.getDomain();
        if (domain.endsWith("/"))
            domain = domain.substring(0, domain.length() - 1);

        this.domainRedirect = domain.concat("/redirect?url=http");
    }

    public String encode(String text) {
        String url = text.replaceAll("/", "%2F");
        url = url.replaceAll("http", domainRedirect);
        return url;
    }

    public String decode(String text) {
        String url = text.replaceAll(Pattern.quote(domainRedirect), "http");
        url = url.replaceAll("%2F", "/");
        return url;
    }
}
