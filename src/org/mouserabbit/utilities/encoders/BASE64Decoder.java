/*-----------------------------------------------------------------------------------
	BASE64Decoder.java
	C	Dec 09 1999
	U	Oct 30 2003 .3

     Dec 09 1999       Initial. Problems with URLEncoder, URLDecoder, BASE64...
     Mar 06 2002       Integrated into CVS
     Aug 09 2003       Modified to be integrated in gaia's prototype class hierarchy
     Oct 30 2003       Package redefinition
     Feb 18 2019       VScode integration
--------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.encoders;

//--------------------------------------------------------------------------------
//   BASE64Decoder
//   I have problems with the sun.misc implementation...
//   This implementation is coming from O'Reilly. Hope it works
//   JAVA Cryptography (Jonathan Knudsen ) : P 259
//--------------------------------------------------------------------------------
public class BASE64Decoder {

     private static final     String              Version = "Base64Decoder V 1.04, Oct 30 2003";

     public byte[] decodeBuffer(String base64) {
          return Base64.decode(base64);
     }
}

