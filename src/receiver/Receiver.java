package receiver;

import utils.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Arrays;

public class Receiver {

    private static final int PORT = 5555;
    private static final String WORK_DIR = "receiver_data";
    private static final String RECEIVED_DOC = WORK_DIR + "/received_document.txt";
    private static final String RECEIVED_SIG = WORK_DIR + "/received_signature.sig";
    private static final String SENDER_PUB = WORK_DIR + "/sender_public.key";
    private static final String VERIFICATION_RESULT = WORK_DIR + "/verification_result.txt";

    public static void main(String[] args) throws Exception {
        new File(WORK_DIR).mkdirs();
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("=== Receiver listening on port " + PORT + " ===");

        while (true) {
            try (Socket client = serverSocket.accept();
                 DataInputStream dis = new DataInputStream(client.getInputStream())) {

                System.out.println("\n=== Connection accepted from sender ===");

                int docLen = dis.readInt();
                byte[] docBytes = new byte[docLen];
                dis.readFully(docBytes);
                System.out.println("Document received. Length: " + docLen + " bytes");
                System.out.println("Document content (first 200 chars):\n" +
                        new String(docBytes, 0, Math.min(200, docBytes.length)));

                int sigLen = dis.readInt();
                byte[] sigBytes = new byte[sigLen];
                dis.readFully(sigBytes);
                System.out.println("Signature received. Length: " + sigLen + " bytes");
                System.out.println("Signature (first 32 bytes hex): " +
                        HashUtil.bytesToHex(Arrays.copyOf(sigBytes, 32)));

                int pubLen = dis.readInt();
                byte[] pubBytes = new byte[pubLen];
                dis.readFully(pubBytes);
                String pubB64 = new String(pubBytes, "UTF-8");
                FileUtil.writeString(SENDER_PUB, pubB64);
                PublicKey senderPubKey = ECDSAUtil.publicKeyFromBase64(pubB64);
                System.out.println("Sender public key received. Length: " + pubLen + " bytes");

                FileUtil.writeBytes(RECEIVED_DOC, docBytes);
                FileUtil.writeBytes(RECEIVED_SIG, sigBytes);
                System.out.println("Document saved at: " + RECEIVED_DOC);
                System.out.println("Signature saved at: " + RECEIVED_SIG);

                boolean valid = ECDSAUtil.verify(docBytes, sigBytes, senderPubKey);
                FileUtil.writeString(VERIFICATION_RESULT, "Signature valid? " + valid);
                System.out.println("Signature verification result: " + valid);
                System.out.println("Verification result saved at: " + VERIFICATION_RESULT);

                System.out.println("=== Processing completed for this package ===\n");
            }
        }
    }
}
