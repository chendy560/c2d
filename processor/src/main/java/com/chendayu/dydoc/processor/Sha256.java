package com.chendayu.dydoc.processor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 考虑到直接写哈希并不难，所以这次没用guava
 */
public class Sha256 {

    private static final char[] HEX = new char[]{
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    private static final ThreadLocal<MessageDigest> SHA_256 =
            ThreadLocal.withInitial(Sha256::getMessageDigest);

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
