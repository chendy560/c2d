package com.chendayu.c2d.processor.util;

public class RequestMappings {

    private RequestMappings() {
    }

    public static String findRequestMapping(String[] value, String[] path) {
        if (value.length > 0 && !value[0].isEmpty()) {
            return value[0];
        }
        if (path.length > 0 && !path[0].isEmpty()) {
            return path[0];
        }
        return "";
    }
}
