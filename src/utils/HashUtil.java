package utils;

import java.security.MessageDigest;

public class HashUtil {

    public static byte[] sha256Bytes(byte[] input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input);
    }

    public static byte[] sha512Bytes(byte[] input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        return md.digest(input);
    }

    public static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
