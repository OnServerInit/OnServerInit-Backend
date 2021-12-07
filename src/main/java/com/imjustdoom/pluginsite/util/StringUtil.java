package com.imjustdoom.pluginsite.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.regex.Pattern;

public class StringUtil {

    /**
     * Checks if the email input matches a valid regex pattern
     * @param emailAddress - Email address inputted
     * @param regexPattern - The regex pattern
     * @return - Returns if the email is a valid email
     */
    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
