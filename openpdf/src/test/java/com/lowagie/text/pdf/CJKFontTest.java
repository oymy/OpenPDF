package com.lowagie.text.pdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import static com.lowagie.text.pdf.BaseFont.RESOURCE_PATH;
import static com.lowagie.text.pdf.BaseFont.getResourceStream;
import static java.lang.Byte.toUnsignedInt;

/**
 * Created by manyan.ouyang ON 2023/11/3
 */
class CJKFontTest {

    @Test
    void test_equals() {
        String fontName = "UniJIS-UCS2-HW-V";
        Assertions.assertArrayEquals(CJKFont.readCMap(fontName), readCMapUsingBytes(fontName));
        Assertions.assertArrayEquals(CJKFont.readCMap(fontName), readCMapUsingBufferedStream(fontName));
    }

    @Test
    /**
     * if open pdf is imported as jar ,then the test is performance loading from jar
     */
    void test_read_cmap_performance() {
        Instant tp0 = Instant.now();
        int cnt = 1000;
        String fontName = "UniJIS-UCS2-HW-V";
        for (int i = 0; i < cnt; i++) {
            readCMapUsingBytes(fontName);
        }

        Instant tp1 = Instant.now();
        for (int i = 0; i < cnt; i++) {
            readCMapUsingBufferedStream(fontName);
        }

        Instant tp2 = Instant.now();
        for (int i = 0; i < cnt; i++) {
            CJKFont.readCMap(fontName);
        }

        Instant tp3 = Instant.now();

        long timeCostWithBytes = (tp1.toEpochMilli() - tp0.toEpochMilli());
        long timeCostWithBufferedStream = (tp2.toEpochMilli() - tp1.toEpochMilli());
        long timeCostOld = (tp3.toEpochMilli() - tp2.toEpochMilli());


        System.out.println("bytes: " + timeCostWithBytes);
        System.out.println("bufferedStream: " + timeCostWithBufferedStream);
        System.out.println("old: " + timeCostOld);

    }

    static char[] readCMapUsingBytes(String name) {

        name = name + ".cmap";
        int size = 0x10000;
        char[] c = new char[size];
        try {
            byte[] bytes = getResourceAsBytes(RESOURCE_PATH + name, size * 2);
            for (int k = 0; k < size; ++k) {
                c[k] = (char) (((toUnsignedInt(bytes[2 * k])) << 8) + (toUnsignedInt(bytes[2 * k + 1])));
            }
            return c;
        } catch (Exception e) {
            // empty on purpose
        }
        return null;
    }

    static byte[] getResourceAsBytes(String key, int size) throws IOException {
        try (InputStream is = getResourceStream(key)) {
            byte[] b = new byte[size];
            is.read(b);
            return b;
        }
    }

    static char[] readCMapUsingBufferedStream(String name) {

        try {
            name = name + ".cmap";
            InputStream is = new BufferedInputStream(getResourceStream(RESOURCE_PATH + name));
            char[] c = new char[0x10000];
            for (int k = 0; k < 0x10000; ++k) {
                c[k] = (char) ((is.read() << 8) + is.read());
            }
            is.close();
            return c;
        } catch (Exception e) {
            // empty on purpose
        }
        return null;
    }

}