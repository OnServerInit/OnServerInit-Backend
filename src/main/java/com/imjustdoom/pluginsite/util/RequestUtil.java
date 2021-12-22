package com.imjustdoom.pluginsite.util;

import java.util.HashMap;

public class RequestUtil {
    public static HashMap<String, String> getParams(String request){
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = request.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }
}
