package ru.sooslick.scpcb.map;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class RMeshReader {
    private final String filename;
    private final byte[] content;
    private int caret = 0;

    public RMeshReader(String filename) {
        this.filename = filename;
        File f = new File(filename);
        if (!f.exists())
            throw new RuntimeException("Error reading file " + filename);
        try {
            content = Files.readAllBytes(Paths.get(f.getPath()));
        } catch (Exception e) {
            throw new RuntimeException("Error reading file " + filename);
        }
    }

    public String readString() {
        int length = readInt();
        String result = new String(Arrays.copyOfRange(content, caret, caret + length));
        caret += length;
        return result;
    }

    public int readInt() {
        int length = 0;
        for (int i = 3; i >= 0; i--)
            length = (length << 8) + (content[caret + i] & 0xFF);
        caret += 4;
        return length;
    }

    public byte readByte() {
        return content[caret++];
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public boolean isEof() {
        return caret>= content.length;
    }
}
