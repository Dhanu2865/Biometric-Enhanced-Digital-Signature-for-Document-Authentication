package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    public static void writeBytes(String path, byte[] data) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(data);
        }
    }

    public static byte[] readBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static void writeString(String path, String data) throws IOException {
        writeBytes(path, data.getBytes("UTF-8"));
    }

    public static String readString(String path) throws IOException {
        return new String(readBytes(path), "UTF-8");
    }
}
