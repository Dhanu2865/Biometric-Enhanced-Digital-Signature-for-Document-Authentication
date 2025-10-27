package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESUtil {

    private static final String TRANSFORM = "AES/CBC/PKCS5Padding";

    public static SecretKeySpec getAesKeyFromBioBytes(byte[] bio256) {
        byte[] key = Arrays.copyOf(bio256, 16); // AES-128
        return new SecretKeySpec(key, "AES");
    }

    public static byte[] encrypt(byte[] plaintext, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORM);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherBytes = cipher.doFinal(plaintext);
        byte[] out = new byte[iv.length + cipherBytes.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(cipherBytes, 0, out, iv.length, cipherBytes.length);
        return out;
    }

    public static byte[] decrypt(byte[] ivAndCipher, SecretKeySpec key) throws Exception {
        byte[] iv = Arrays.copyOfRange(ivAndCipher, 0, 16);
        byte[] cipherBytes = Arrays.copyOfRange(ivAndCipher, 16, ivAndCipher.length);
        Cipher cipher = Cipher.getInstance(TRANSFORM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(cipherBytes);
    }
}
