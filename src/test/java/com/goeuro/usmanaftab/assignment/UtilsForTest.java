package com.goeuro.usmanaftab.assignment;

import java.io.*;

/**
 * 
 * @author usmanaftab
 *
 */
public class UtilsForTest {

    private UtilsForTest() {
        throw new AssertionError();
    }

    public static String readFile(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        return readFile(fileInputStream);
    }

    public static String readFileFromStream(String fileName) throws IOException {
        InputStream inputStream = UtilsForTest.class.getResourceAsStream(fileName);
        return readFile(inputStream);
    }

    public static String readFile(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String output = null;
        while((output = br.readLine()) != null) {
            sb.append(output);
        }
        return sb.toString();
    }

    public static String getStringOf(StringReader sr) throws IOException {
        if (sr == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        int charsRead = -1;
        char[] chars = new char[100];
        while((charsRead = sr.read(chars, 0, chars.length)) > 0) {
            builder.append(chars, 0, charsRead);
        }
        return builder.toString();
    }
}
