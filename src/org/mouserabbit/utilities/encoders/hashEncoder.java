/*--------------------------------------------------------------------------------------------------
    hashEncoder.java

    jan 18 2010     Initial ant utilities
    Feb 03 2010     Some logging
    Feb 04 2010     Generalize
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

public class hashEncoder {

    protected static final String Version = "hashEncoder 1.02, Feb 04 2010 "; 
    
    protected static Logger log = Logger.getLogger(hashEncoder.class.getName());

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
                log.debug(Version + "        " + propertyname + " = " + provider.getProperty(propertyname));
            }
        }
    }

    public static void JCEinfo(boolean detailsrequested, String propertiesfilter) {
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
                /*
                 * Check the property name matches the specified filter
                 */
                if(propertyname.toUpperCase().contains(propertiesfilter.toUpperCase())) {
                    log.debug(Version + "        " + propertyname + " = " + provider.getProperty(propertyname));
                }
            }
        }
    }
 
    /*
     * Returns a base64 hash string
     */
    public static String hash(String text, String providername, String algo)  throws NoSuchAlgorithmException, UnsupportedEncodingException  {
        
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


    public String getVersion() {
        return Version;
    }
}


