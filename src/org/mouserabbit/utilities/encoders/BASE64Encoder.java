/*-----------------------------------------------------------------------------------
	BASE64Encoder.java
	C	Dec 09 1999
	U	Oct 30 2003    .4

     Dec 09 1999       Initial. Problems with URLEncoder, URLDecoder, BASE64...
     Mar 06 2002       integrated into CVS  
     Aug 09 2003       Modified to be integrated in gaia's prototype class hierarchy
     Oct 30 2003       Package redefinition
     Feb 18 2019       VScode integration
     Feb 19 2019       Version
--------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.encoders;

//--------------------------------------------------------------------------------
//   BASE64
//   I have problems with the sun.misc implementation...
//   This implementation is coming from O'Reilly. Hope it works
//   JAVA Cryptography (Jonathan Knudsen ) : P 259
//===========================================================================
public class BASE64Encoder {

     private static final     String              Version = "Base64Encoder 1.06, Feb 19 2019";

     // ---------------------------------------------------------------------------
     // Version
     // ---------------------------------------------------------------------------
     public static String getVersion() {
          return Version;
     }

     public String encodeBuffer(byte[] raw) {
          return Base64.encode(raw);
     }
}

    