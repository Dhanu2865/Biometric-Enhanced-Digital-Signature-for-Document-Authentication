package utils;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class ECDSAUtil {

    public static KeyPair generateECKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);
        return kpg.generateKeyPair();
    }

    public static byte[] sign(byte[] data, PrivateKey priv) throws Exception {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(priv);
        sig.update(data);
        return sig.sign();
    }

    public static boolean verify(byte[] data, byte[] signatureBytes, PublicKey pub) throws Exception {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(pub);
        sig.update(data);
        return sig.verify(signatureBytes);
    }

    public static String publicKeyToBase64(PublicKey pk) {
        return Base64.getEncoder().encodeToString(pk.getEncoded());
    }

    public static String privateKeyToBase64(PrivateKey pk) {
        return Base64.getEncoder().encodeToString(pk.getEncoded());
    }

    public static PublicKey publicKeyFromBase64(String b64) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(b64);
        KeyFactory kf = KeyFactory.getInstance("EC");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return kf.generatePublic(spec);
    }

    public static PrivateKey privateKeyFromBase64(String b64) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(b64);
        KeyFactory kf = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return kf.generatePrivate(spec);
    }
}
