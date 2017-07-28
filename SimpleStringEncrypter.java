
package com.jaison;

import java.io.*;
import java.util.Arrays;

public class SimpleStringEncrypter {
    private static final byte[] PASSWORD_ENCRYPTION_KEY = "sdf@!#$verf^wv%6Fwe%$$#FFGwfsdefwfe135s$^H)dg".getBytes();
    private static final String CHARSET = "UTF8";
    public static void main(String[] args) throws Exception{
        SimpleStringEncrypter simpleStringEncrypter = new SimpleStringEncrypter();
        //System.out.println(simpleStringEncrypter.encrypt("Hello"));
        // Get password from console
        System.err.print("Please enter encrypted dbeaver password(file:.dbeaver-data-sources.xml): ");
        String password = System.console().readLine();
        // Print passowrd to see extraneous chars
        System.err.println("\nYou have entered : \"" + password + "\"");
        System.out.print(simpleStringEncrypter.decrypt(password));
        System.err.println("");
    }
    public SimpleStringEncrypter() {
    }

    public String encrypt(String unencryptedString) throws Exception {
        if(unencryptedString == null) {
            throw new IllegalArgumentException("Empty string");
        } else {
            try {
                byte[] e = unencryptedString.getBytes("UTF8");
                byte[] plainBytes = Arrays.copyOf(e, e.length + 2);
                plainBytes[plainBytes.length - 2] = 0;
                plainBytes[plainBytes.length - 1] = -127;
                this.xorStringByKey(plainBytes);
                return Base64.encode(plainBytes);
            } catch (Exception var4) {
                throw new Exception(var4);
            }
        }
    }

    private void xorStringByKey(byte[] plainBytes) throws UnsupportedEncodingException {
        int keyOffset = 0;

        for(int i = 0; i < plainBytes.length; ++i) {
            byte keyChar = PASSWORD_ENCRYPTION_KEY[keyOffset];
            ++keyOffset;
            if(keyOffset >= PASSWORD_ENCRYPTION_KEY.length) {
                keyOffset = 0;
            }

            plainBytes[i] ^= keyChar;
        }

    }

    public String decrypt(String encryptedString) throws Exception {
        if(encryptedString != null && encryptedString.trim().length() > 0) {
            try {
                byte[] e = Base64.decode(encryptedString);
                this.xorStringByKey(e);
                if(e[e.length - 2] == 0 && e[e.length - 1] == -127) {
                    return new String(e, 0, e.length - 2, "UTF8");
                } else {
                    throw new Exception("Invalid encrypted string");
                }
            } catch (Exception var3) {
                throw new Exception(var3);
            }
        } else {
            throw new IllegalArgumentException("Empty encrypted string");
        }
    }
}

 final class Base64 {

    private static final char[] S_BASE64CHAR = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '+', '/'
    };

    private static final char S_BASE64PAD = '=';

    private static final byte[] S_DECODETABLE = new byte[128];

    static {
        for (int i = 0;  i < S_DECODETABLE.length;  i ++)
            S_DECODETABLE[i] = Byte.MAX_VALUE;  // 127
        for (int i = 0;  i < S_BASE64CHAR.length;  i ++) // 0 to 63
            S_DECODETABLE[S_BASE64CHAR[i]] = (byte)i;
    }

    private static int decode0(char[] ibuf, byte[] obuf, int wp) {
        int outlen = 3;
        if (ibuf[3] == S_BASE64PAD)  outlen = 2;
        if (ibuf[2] == S_BASE64PAD)  outlen = 1;
        int b0 = S_DECODETABLE[ibuf[0]];
        int b1 = S_DECODETABLE[ibuf[1]];
        int b2 = S_DECODETABLE[ibuf[2]];
        int b3 = S_DECODETABLE[ibuf[3]];
        switch (outlen) {
            case 1:
                obuf[wp] = (byte)(b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                return 1;
            case 2:
                obuf[wp++] = (byte)(b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                obuf[wp] = (byte)(b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
                return 2;
            case 3:
                obuf[wp++] = (byte)(b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                obuf[wp++] = (byte)(b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
                obuf[wp] = (byte)(b2 << 6 & 0xc0 | b3 & 0x3f);
                return 3;
            default:
                throw new RuntimeException("Internal Errror");
        }
    }

    /**
     *
     */
    public static byte[] decode(char[] data, int off, int len) {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[len/4*3+3];
        int obufcount = 0;
        for (int i = off;  i < off+len;  i ++) {
            char ch = data[i];
            if (ch == S_BASE64PAD
                    || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != Byte.MAX_VALUE) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    obufcount += decode0(ibuf, obuf, obufcount);
                }
            }
        }
        if (obufcount == obuf.length)
            return obuf;
        byte[] ret = new byte[obufcount];
        System.arraycopy(obuf, 0, ret, 0, obufcount);
        return ret;
    }

    /**
     *
     */
    public static byte[] decode(String data) {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[data.length()/4*3+3];
        int obufcount = 0;
        for (int i = 0;  i < data.length();  i ++) {
            char ch = data.charAt(i);
            if (ch == S_BASE64PAD
                    || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != Byte.MAX_VALUE) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    obufcount += decode0(ibuf, obuf, obufcount);
                }
            }
        }
        if (obufcount == obuf.length)
            return obuf;
        byte[] ret = new byte[obufcount];
        System.arraycopy(obuf, 0, ret, 0, obufcount);
        return ret;
    }

    /**
     *
     */
    public static void decode(char[] data, int off, int len, OutputStream ostream) throws IOException {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[3];
        for (int i = off;  i < off+len;  i ++) {
            char ch = data[i];
            if (ch == S_BASE64PAD
                    || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != Byte.MAX_VALUE) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    int obufcount = decode0(ibuf, obuf, 0);
                    ostream.write(obuf, 0, obufcount);
                }
            }
        }
    }

    /**
     *
     */
    public static void decode(String data, OutputStream ostream) throws IOException {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[3];
        for (int i = 0;  i < data.length();  i ++) {
            char ch = data.charAt(i);
            if (ch == S_BASE64PAD
                    || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != Byte.MAX_VALUE) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    int obufcount = decode0(ibuf, obuf, 0);
                    ostream.write(obuf, 0, obufcount);
                }
            }
        }
    }

    /**
     * Returns base64 representation of specified byte array.
     */
    public static String encode(byte[] data) {
        return encode(data, 0, data.length);
    }

    /**
     * Returns base64 representation of specified byte array.
     */
    public static String encode(byte[] data, int off, int len)
    {
        if (len <= 0)  return "";
        char[] out = new char[len/3*4+4];
        int rindex = off;
        int windex = 0;
        int rest = len-off;
        while (rest >= 3) {
            int i = ((data[rindex]&0xff)<<16)
                    +((data[rindex+1]&0xff)<<8)
                    +(data[rindex+2]&0xff);
            out[windex++] = S_BASE64CHAR[i>>18];
            out[windex++] = S_BASE64CHAR[(i>>12)&0x3f];
            out[windex++] = S_BASE64CHAR[(i>>6)&0x3f];
            out[windex++] = S_BASE64CHAR[i&0x3f];
            rindex += 3;
            rest -= 3;
        }
        if (rest == 1) {
            int i = data[rindex]&0xff;
            out[windex++] = S_BASE64CHAR[i>>2];
            out[windex++] = S_BASE64CHAR[(i<<4)&0x3f];
            out[windex++] = S_BASE64PAD;
            out[windex++] = S_BASE64PAD;
        } else if (rest == 2) {
            int i = ((data[rindex]&0xff)<<8)+(data[rindex+1]&0xff);
            out[windex++] = S_BASE64CHAR[i>>10];
            out[windex++] = S_BASE64CHAR[(i>>4)&0x3f];
            out[windex++] = S_BASE64CHAR[(i<<2)&0x3f];
            out[windex++] = S_BASE64PAD;
        }
        return new String(out, 0, windex);
    }

    /**
     * Outputs base64 representation of the specified byte array to a byte stream.
     */
    public static void encode(byte[] data, int off, int len, OutputStream ostream) throws IOException {
        if (len <= 0)  return;
        byte[] out = new byte[4];
        int rindex = off;
        int rest = len-off;
        while (rest >= 3) {
            int i = ((data[rindex]&0xff)<<16)
                    +((data[rindex+1]&0xff)<<8)
                    +(data[rindex+2]&0xff);
            out[0] = (byte)S_BASE64CHAR[i>>18];
            out[1] = (byte)S_BASE64CHAR[(i>>12)&0x3f];
            out[2] = (byte)S_BASE64CHAR[(i>>6)&0x3f];
            out[3] = (byte)S_BASE64CHAR[i&0x3f];
            ostream.write(out, 0, 4);
            rindex += 3;
            rest -= 3;
        }
        if (rest == 1) {
            int i = data[rindex]&0xff;
            out[0] = (byte)S_BASE64CHAR[i>>2];
            out[1] = (byte)S_BASE64CHAR[(i<<4)&0x3f];
            out[2] = (byte)S_BASE64PAD;
            out[3] = (byte)S_BASE64PAD;
            ostream.write(out, 0, 4);
        } else if (rest == 2) {
            int i = ((data[rindex]&0xff)<<8)+(data[rindex+1]&0xff);
            out[0] = (byte)S_BASE64CHAR[i>>10];
            out[1] = (byte)S_BASE64CHAR[(i>>4)&0x3f];
            out[2] = (byte)S_BASE64CHAR[(i<<2)&0x3f];
            out[3] = (byte)S_BASE64PAD;
            ostream.write(out, 0, 4);
        }
    }

    /**
     * Outputs base64 representation of the specified byte array to a character stream.
     */
    public static void encode(byte[] data, int off, int len, Writer writer) throws IOException {
        if (len <= 0)  return;
        char[] out = new char[4];
        int rindex = off;
        int rest = len-off;
        while (rest >= 3) {
            int i = ((data[rindex]&0xff)<<16)
                    +((data[rindex+1]&0xff)<<8)
                    +(data[rindex+2]&0xff);
            out[0] = S_BASE64CHAR[i>>18];
            out[1] = S_BASE64CHAR[(i>>12)&0x3f];
            out[2] = S_BASE64CHAR[(i>>6)&0x3f];
            out[3] = S_BASE64CHAR[i&0x3f];
            writer.write(out, 0, 4);
            rindex += 3;
            rest -= 3;
/*
            if (output % 76 == 0)
                writer.write("\n");
*/
        }
        if (rest == 1) {
            int i = data[rindex]&0xff;
            out[0] = S_BASE64CHAR[i>>2];
            out[1] = S_BASE64CHAR[(i<<4)&0x3f];
            out[2] = S_BASE64PAD;
            out[3] = S_BASE64PAD;
            writer.write(out, 0, 4);
        } else if (rest == 2) {
            int i = ((data[rindex]&0xff)<<8)+(data[rindex+1]&0xff);
            out[0] = S_BASE64CHAR[i>>10];
            out[1] = S_BASE64CHAR[(i>>4)&0x3f];
            out[2] = S_BASE64CHAR[(i<<2)&0x3f];
            out[3] = S_BASE64PAD;
            writer.write(out, 0, 4);
        }
    }

    /**
     * Outputs base64 representation of the specified input stream to a character stream.
     */
    public static void encode(InputStream stream, long len, Writer writer)
            throws IOException
    {
        if (len <= 0)  return;
        char[] out = new char[4];
        long rest = len;
        while (rest >= 3) {
            int i = ((stream.read()&0xff)<<16)
                    +((stream.read()&0xff)<<8)
                    +(stream.read()&0xff);
            out[0] = S_BASE64CHAR[i>>18];
            out[1] = S_BASE64CHAR[(i>>12)&0x3f];
            out[2] = S_BASE64CHAR[(i>>6)&0x3f];
            out[3] = S_BASE64CHAR[i&0x3f];
            writer.write(out, 0, 4);
            rest -= 3;
        }
        if (rest == 1) {
            int i = stream.read()&0xff;
            out[0] = S_BASE64CHAR[i>>2];
            out[1] = S_BASE64CHAR[(i<<4)&0x3f];
            out[2] = S_BASE64PAD;
            out[3] = S_BASE64PAD;
            writer.write(out, 0, 4);
        } else if (rest == 2) {
            int i = ((stream.read()&0xff)<<8)+(stream.read()&0xff);
            out[0] = S_BASE64CHAR[i>>10];
            out[1] = S_BASE64CHAR[(i>>4)&0x3f];
            out[2] = S_BASE64CHAR[(i<<2)&0x3f];
            out[3] = S_BASE64PAD;
            writer.write(out, 0, 4);
        }
    }

}
