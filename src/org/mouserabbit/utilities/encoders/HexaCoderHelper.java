/*------------------------------------------------------------------------------------------
	HexaCoderHelper.java
	C	Aug 16 2001
	U	Mar 06 2002 .2

     Yves Toubhans : Oracle France : +33 1 47 62 28 02
     Aug 16 2001    Initial.
     Mar 06 2002    Integrated into CVS
     Nov 02 2003    Pakage changed
     Feb 18 2019    VScode integration
-------------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.encoders;

public class HexaCoderHelper {
  private static final char[] DIGITS = 
              {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

     //---------------------------------------------------------------
     // Return the character for the ordinal number provided
     // @param n a single digit number
     // @return A character
     //---------------------------------------------------------------
      public static char chr (int n) {
        return DIGITS[n & 0xF];
      } //chr

     //---------------------------------------------------------------
     // Encode a String as Hexidecimal Characters
     // @param str String to be Encoded
     // @return String of Hexidecimal Characters
     //---------------------------------------------------------------
      public static String encode (String str) {
        if(str != null) {
          char[] in = str.toCharArray();
          StringBuffer out = new StringBuffer(in.length * 2);
          int i = 0;
          while(i < in.length) {
            out.append(chr(in[i] >> 4)).append(chr(in[i++]));
          }
          return out.toString();
        }
        return null;
      } //encode

     //---------------------------------------------------------------
     // Encode a String as Hexidecimal Characters
     // @param Array of bytes to be Encoded
     // @return String of Hexidecimal Characters
     //---------------------------------------------------------------
      public static String encode (byte[] in) {
        if(in.length != 0) {
          StringBuffer out = new StringBuffer(in.length * 2);
          int i = 0;
          while(i < in.length) {
            out.append(chr(in[i] >> 4)).append(chr(in[i++]));
          }
          return out.toString();
        }
        return null;
      } //encode

     //---------------------------------------------------------------
     // Return the ordinal number of the character provided
     // @param c One of (0 - 9, A - F, a - f)
     // @return An integer between 0 and 15
     //---------------------------------------------------------------
      public static int ord (char c) {
        if(c <= '9') {
          return ((int)c) - ((int)'0');
        } else if(c <= 'F') {
          return ((int)c) - ((int)'A') + 10;
        } else {
          return ((int)c) - ((int)'a') + 10;
        }
      } //ord

     //---------------------------------------------------------------
     // Convert a Hexidecimal String into a Character String
     // @param str A String of Hexidecimal Characters
     // @return A String
     //---------------------------------------------------------------
      public static String decode (String str) {
        if(str != null) {
          char[] in = str.toCharArray();
          StringBuffer out = new StringBuffer(in.length / 2);
          int i = 0;
          while(i < in.length) {
            out.append((char)((ord(in[i++]) << 4) | ord(in[i++])));
          }
          return out.toString();
        }
        return null;
      } //decode

}

