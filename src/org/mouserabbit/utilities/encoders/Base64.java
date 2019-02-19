/*-----------------------------------------------------------------------------------
	Base64.java
	C	Dec 09 1999
	U	Oct 30 2003 .8

     Dec 09 1999    Initial. Problems with URLEncoder, URLDecoder, BASE64...
     Dec 10 1999    Fix String index overflow problem when decoding.
     Jul 05 2001    Comptatibility with Jdev 5.
     Mar 06 2002    Integrated into CVS
     Aug 09 2003    Modified to be integrated in gaia's prototype class hierarchy
     Oct 30 2003    Package redefinition
     Feb 18 2019    VScode integration
     Feb 19 2019    Get version
--------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.encoders;

//--------------------------------------------------------------------------------
//   BASE64
//   I have problems with the sun.misc implementation...
//   This implementation is coming from O'Reilly. Hope it works
//   JAVA Cryptography (Jonathan Knudsen ) : P 259
//--------------------------------------------------------------------------------
public class Base64 {

     private static final String Version = "Base64 V 1.10, Feb 19 2019";

     // ---------------------------------------------------------------------------
     // Version
     // ---------------------------------------------------------------------------
     public static String getVersion() {
          return Version;
     }
     // ---------------------------------------------------------------------------
     // Encoding
     // ---------------------------------------------------------------------------
     public static String encode(byte[] raw) {
          StringBuffer encoded = new StringBuffer();
          for (int i = 0; i < raw.length; i += 3) {
               encoded.append(encodeBlock(raw, i));
          }
          int len = encoded.length();
          return encoded.toString();
     }

     // ---------------------------------------------------------------------------
     // Encoding a block
     // ---------------------------------------------------------------------------
     protected static char[] encodeBlock(byte[] raw, int offset) {
          int block = 0;
          int slack = raw.length - offset - 1;
          int end = (slack >= 2) ? 2 : slack;
          for (int i = 0; i <= end; i++) {
               byte b = raw[offset + i];
               int neuter = (b < 0) ? b + 256 : b;
               block += neuter << (8 * (2 - i));
          }
          // Block built, just extract the BASE64 digits
          char[] base64 = new char[4];
          for (int i = 0; i < 4; i++) {
               int sixbit = (block >> (6 * (3 - i))) & 0x3f;
               base64[i] = getChar(sixbit);
          }
          // Padding
          if (slack < 1)
               base64[2] = '=';
          if (slack < 2)
               base64[3] = '=';
          return base64;
     }

     // ---------------------------------------------------------------------------
     // Encoding 6 bits
     // ---------------------------------------------------------------------------
     protected static char getChar(int sixBit) {
          if (sixBit >= 0 && sixBit <= 25)
               return (char) ('A' + sixBit);
          if (sixBit >= 26 && sixBit <= 51)
               return (char) ('a' + (sixBit - 26));
          if (sixBit >= 52 && sixBit <= 61)
               return (char) ('0' + (sixBit - 52));
          if (sixBit == 62)
               return '+';
          if (sixBit == 63)
               return '/';
          return '?';
     }

     // ---------------------------------------------------------------------------
     // Decoding
     // Modified Dec 10 99 . Check i in the loop to avoid exception
     // ---------------------------------------------------------------------------
     public static byte[] decode(String base64) {
          int pad = 0;
          // Count padding digits
          for (int i = base64.length() - 1; base64.charAt(i) == '='; i--) {
               pad++;
          }
          int length = base64.length() * 6 / 8 - pad;
          byte[] raw = new byte[length];
          // Loop through the base64 value
          int rawIndex = 0;
          int block;
          for (int i = 0; i < base64.length(); i += 4) {
               /*
                * int block = (getValue(base64.charAt(i)) << 18) + (getValue(base64.charAt(i +
                * 1)) << 12) + (getValue(base64.charAt(i + 2)) << 6) +
                * (getValue(base64.charAt(i + 3)));
                */
               block = (getValue(base64.charAt(i)) << 18);
               if ((i + 1) < base64.length())
                    block += (getValue(base64.charAt(i + 1)) << 12);
               if ((i + 2) < base64.length())
                    block += (getValue(base64.charAt(i + 2)) << 6);
               if ((i + 3) < base64.length())
                    block += (getValue(base64.charAt(i + 3)));

               for (int j = 0; j < 3 && rawIndex + j < raw.length; j++) {
                    raw[rawIndex + j] = (byte) ((block >> (8 * (2 - j))) & 0xff);
               }
               rawIndex += 3;
          }
          return raw;
     }

     // ---------------------------------------------------------------------------
     // Decoding a charcater to 6 bits
     // ---------------------------------------------------------------------------
     protected static int getValue(char c) {
          if (c >= 'A' && c <= 'Z')
               return c - 'A';
          if (c >= 'a' && c <= 'z')
               return c - 'a' + 26;
          if (c >= '0' && c <= '9')
               return c - '0' + 52;
          if (c == '+')
               return 62;
          if (c == '/')
               return 63;
          if (c == '=')
               return 0;
          return -1;
     }
}
