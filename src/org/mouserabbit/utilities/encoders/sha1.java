/*--------------------------------------------------------------------------------------------------
    sha1.java

    jan 18 2010     Initial ant utilities
    Feb 03 2010     Some logging
    Feb 18 2019     VScode integration
---------------------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.encoders;


import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import java.util.Enumeration;

import org.apache.log4j.Logger;


public class sha1 {

    protected static final String Version = "sha1 1.09, Feb 04 2010 ";      // TODO
    
    protected static Logger log = Logger.getLogger(sha1.class.getName());


 
    protected static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static void JCEinfo(boolean detailsrequested) {
        log.info(Version + "JCE information");
        Provider[] providerlist = null;
        Provider provider = null;


        providerlist = Security.getProviders();
        log.info(Version + "There are " + providerlist.length + " providers enlisted.");
        for (int i = 0; i < providerlist.length; ++i) {
            provider = providerlist[i];
            log.info(Version + provider.getName());
            /*
                * Dump some detailed provider information
                *
                * This provider list comes from $JAVA_HOME/jre/lib/security/java.security
                *
                * security.provider.1=com.sun.crypto.provider.SunJCE
                * security.provider.2=au.net.aba.crypto.provider.ABAProvider
                * security.provider.3=cryptix.jce.provider.CryptixCrypto
                * security.provider.4=�
                */
            Enumeration en = provider.propertyNames();
            String propertyname = null;
            while (en.hasMoreElements() && (detailsrequested)) {
                propertyname = (String)en.nextElement();
                log.debug(Version + "        " + propertyname + " = " +
                          provider.getProperty(propertyname));
            }
        }
    }
 
    /*
     * Returns a base64 encoded string
     */
    public static String SHA1String(String text, String providername, String algo)  throws NoSuchAlgorithmException, UnsupportedEncodingException  {
        
        Provider requestedprovider = null;
        
        try {
            requestedprovider = Security.getProvider(providername);
        }
        catch( Exception e) {
            log.error(Version + "Unable to get the requested provider : " + providername);
        }
        
        MessageDigest md = MessageDigest.getInstance(algo, requestedprovider);
        byte[] sha1hash = new byte[text.length()];
        md.update(text.getBytes());
        sha1hash = md.digest();
        return Base64.encode(sha1hash);
    }

    public static byte[] SHA1(String text, String providername, String algo)  throws NoSuchAlgorithmException, UnsupportedEncodingException  {
        
        Provider requestedprovider = null;
        
        try {
            requestedprovider = Security.getProvider(providername);
        }
        catch( Exception e) {
            log.error(Version + "Unable to get the requested provider : " + providername);
        }
        
        MessageDigest md = MessageDigest.getInstance(algo, requestedprovider);
        byte[] sha1hash = new byte[text.length()];
        md.update(text.getBytes());
        sha1hash = md.digest();
        return sha1hash;
    }

    public static byte[] SHA1(String text)  throws NoSuchAlgorithmException, UnsupportedEncodingException  {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] sha1hash = new byte[text.length()];
        md.update(text.getBytes());
        sha1hash = md.digest();
        return sha1hash;
    }

    public String getVersion() {
        return Version;
    }
}


