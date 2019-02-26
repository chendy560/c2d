package com.chendayu.dydoc.processor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    private static final char[] HEX = new char[]{
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };
    private static final ThreadLocal<MessageDigest> SHA_256 =
            ThreadLocal.withInitial(Utils::getMessageDigest);

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

    public static String lowerCaseFirst(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            char[] chars = s.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }

        return s;
    }

    public static String shortHash(String s) {
        byte[] bytes = SHA_256.get().digest(s.getBytes(StandardCharsets.UTF_8));
        return bytesToHexString(bytes).substring(0, 7);
    }

    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int n = i * 2;
            byte b = bytes[i];
            chars[n + 1] = HEX[b & 15];
            chars[n] = HEX[(b >> 4) & 15];
        }
        return new String(chars);
    }
}
