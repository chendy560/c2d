package com.chendayu.c2d.processor.util;

public class RequestParameters {

    private RequestParameters() {
    }

    public static String findParameterName(String defaultName, String... names) {
        for (String name : names) {
            if (name != null && !name.isEmpty()) {
                return name;
            }
        }
        return defaultName;
    }
}
