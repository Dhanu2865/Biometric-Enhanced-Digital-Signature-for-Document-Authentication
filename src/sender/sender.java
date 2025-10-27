package sender;

import utils.*;
import javax.crypto.spec.SecretKeySpec;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class sender {

    private static final String WORK_DIR = "sender_data";
    private static final String ENCRYPTED_PRIVATE_KEY_FILE = WORK_DIR + "/sender_private_encrypted.bin";
    private static final String PUBLIC_KEY_FILE = WORK_DIR + "/sender_public.key";
    private static final String DOCUMENT_FILE = WORK_DIR + "/message.txt";
    private static final String SIGNATURE_FILE = WORK_DIR + "/signature.sig";
    private static final String FINGERPRINT_FILE = "../fingerprint_sample/fingerprint.jpg";

    private static final String RECEIVER_HOST = "localhost";
    private static final int RECEIVER_PORT = 5555;

    public static void main(String[] args) throws Exception {

        new File(WORK_DIR).mkdirs();

        System.out.println("=== Generating/loading keypair ===");
        File encPrivFile = new File(ENCRYPTED_PRIVATE_KEY_FILE);
        File pubFile = new File(PUBLIC_KEY_FILE);

        if (!pubFile.exists() || !encPrivFile.exists()) {
            System.out.println("Generating EC keypair...");
            KeyPair kp = ECDSAUtil.generateECKeyPair();
            String pubB64 = ECDSAUtil.publicKeyToBase64(kp.getPublic());
            String privB64 = ECDSAUtil.privateKeyToBase64(kp.getPrivate());

            // Save public key
            FileUtil.writeString(PUBLIC_KEY_FILE, pubB64);
            System.out.println("Public key saved at: " + PUBLIC_KEY_FILE);

            // Load fingerprint image and derive AES key
            BufferedImage img = ImageIO.read(new File(FINGERPRINT_FILE));
            byte[] pixels = new byte[img.getWidth() * img.getHeight()];
            int idx = 0;
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int rgb = img.getRGB(x, y);
                    int gray = (rgb >> 16) & 0xff;
                    pixels[idx++] = (byte) gray;
                }
            }
            byte[] bioKey = HashUtil.sha256Bytes(pixels);
            System.out.println("Fingerprint hash (first 16 bytes): " + HashUtil.bytesToHex(Arrays.copyOf(bioKey, 16)));

            SecretKeySpec aesKey = AESUtil.getAesKeyFromBioBytes(bioKey);

            // Encrypt private key
            byte[] privBytes = privB64.getBytes("UTF-8");
            byte[] encPriv = AESUtil.encrypt(privBytes, aesKey);
            FileUtil.writeBytes(ENCRYPTED_PRIVATE_KEY_FILE, encPriv);
            System.out.println("Encrypted private key saved at: " + ENCRYPTED_PRIVATE_KEY_FILE);

            // Sample document
            //FileUtil.writeString(DOCUMENT_FILE, "Confidential document. Date: 2025-10-27");
            System.out.println("Sample document created at: " + DOCUMENT_FILE);
        }

        System.out.println("=== Loading fingerprint and decrypting private key ===");
        BufferedImage img = ImageIO.read(new File(FINGERPRINT_FILE));
        byte[] pixels = new byte[img.getWidth() * img.getHeight()];
        int idx = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int gray = (rgb >> 16) & 0xff;
                pixels[idx++] = (byte) gray;
            }
        }
        byte[] bioKey = HashUtil.sha256Bytes(pixels);
        SecretKeySpec aesKey = AESUtil.getAesKeyFromBioBytes(bioKey);
        System.out.println("Fingerprint hash (first 16 bytes): " + HashUtil.bytesToHex(Arrays.copyOf(bioKey, 16)));

        byte[] encPriv = FileUtil.readBytes(ENCRYPTED_PRIVATE_KEY_FILE);
        byte[] privBytes = AESUtil.decrypt(encPriv, aesKey);
        PrivateKey privateKey = ECDSAUtil.privateKeyFromBase64(new String(privBytes, "UTF-8"));
        System.out.println("Private key decrypted successfully.");

        String pubB64 = FileUtil.readString(PUBLIC_KEY_FILE);
        PublicKey publicKey = ECDSAUtil.publicKeyFromBase64(pubB64);
        System.out.println("Public key loaded successfully.");

        System.out.println("=== Reading document and signing ===");
        byte[] documentBytes = FileUtil.readBytes(DOCUMENT_FILE);
        System.out.println("Document content:\n" + new String(documentBytes, "UTF-8"));

        byte[] signatureBytes = ECDSAUtil.sign(documentBytes, privateKey);
        FileUtil.writeBytes(SIGNATURE_FILE, signatureBytes);
        System.out.println("Document signed. Signature (first 32 bytes hex): " + HashUtil.bytesToHex(Arrays.copyOf(signatureBytes, 32)));

        System.out.println("=== Sending package to receiver ===");
        try (Socket socket = new Socket(RECEIVER_HOST, RECEIVER_PORT);
             OutputStream out = socket.getOutputStream();
             DataOutputStream dos = new DataOutputStream(out)) {

            dos.writeInt(documentBytes.length);
            dos.write(documentBytes);

            dos.writeInt(signatureBytes.length);
            dos.write(signatureBytes);

            byte[] pubBytes = pubB64.getBytes("UTF-8");
            dos.writeInt(pubBytes.length);
            dos.write(pubBytes);

            System.out.println("Package sent to receiver.");
        }

        System.out.println("=== Sender process completed ===");
    }
}
