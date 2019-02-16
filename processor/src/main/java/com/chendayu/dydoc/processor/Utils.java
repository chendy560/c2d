package com.chendayu.dydoc.processor;

public class Utils {

    private Utils() {
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

    public static String findName(String defaultName, String... names) {
        for (String name : names) {
            if (name != null && !name.isEmpty()) {
                return name;
            }
        }
        return defaultName;
    }

    public static String upperCaseFirst(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            char[] chars = s.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }

        return s;
    }
}
