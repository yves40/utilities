/*-----------------------------------------------------------------------------------
	XMLHelper.java

          Aug 30 2001   Initial : Thanks to Steve Muench, and its great book on XML
          Nov 27 2001   Add methods without baseurl parameter
          Dec 04 2001   Add URL 
          Dec 05 2001   Add new methods, using oracle provided xml code 
          Mar 06 2002   Integrated into CVS
          Aug 09 2003   Modified to be integrated in gaia's prototype class hierarchy
          Oct 31 2003   Package redefinition
          Aug 04 2010   Remove some cast
          Feb 18 2019   VScode integration
--------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.helpers;

import org.w3c.dom.*;
import oracle.xml.parser.v2.*;
import org.xml.sax.*;
import java.io.*;
import java.net.*;
//--------------------------------------------------------------------------------
// 
//--------------------------------------------------------------------------------
public class XMLHelper {

      static final String      Version = "xmlhelper V 1.10, Aug 04 2010";

      // ===============================================
      // Returns an element value from an XML file, given 
      // a path, and an element name
      // ===============================================
      public static String getElement(String fileurl, String xpath, 
                                   String elementname) throws Exception {
          URL xmlurl = XMLHelper.newURL(fileurl);
          DOMParser dp = new DOMParser();
          try {
               dp.parse(xmlurl);
          }
          catch(SAXParseException spe) {
               throw new Exception("xmlhelper : xmlresource " + fileurl + " is not well-formed.");
          }
          catch(FileNotFoundException fnfe) {
               throw new Exception("xmlhelper : xml resource " + fileurl + " not found.");
          }
          // Get an XMLDocument
          XMLDocument xmldoc = dp.getDocument();
          NodeList nl = null;
          try {
               // Select matching nodes
               nl = xmldoc.selectNodes(xpath);
          }
          catch(XSLException xse){
               throw new Exception(xse.getMessage());
          }
          int found = nl.getLength();
          if(found == 1) {
               XMLNode curNode = (XMLNode)nl.item(0);
               return curNode.valueOf(elementname);
          }
          if(found != 1)     // have to get a single element
               throw new Exception("xmlhelper : Multiple nodes selected, invalid request");
          if(found != 0)     // have to get a single element
               throw new Exception("xmlhelper : No node selected, invalid request");
          return null;
      }
      // ===============================================
      // Returns the library version
      // ===============================================
      public static String getVersion() {
          return Version;
      }
      // ===============================================
      // Parse an XML document from a character reader
      // ===============================================
      public static XMLDocument parse(Reader r, URL baseurl)
                  throws IOException, SAXParseException, SAXException {

          // Construct an input source from the reader
          InputSource input = new InputSource(r);
          // Set the base URL if provided
          if(baseurl != null)input.setSystemId(baseurl.toString());
          // Construct a new DOM parser
          DOMParser xp = new  DOMParser();
          // Parse in non-validating mode
          // xp.setValidationMode(false);
          // Preserve whitespace
          xp.setPreserveWhitespace(true);
          // Attempt to parse XML coming from the Reader
          xp.parse(input);
          return xp.getDocument();
      }
      public static XMLDocument parse(Reader r)
                  throws IOException, SAXParseException, SAXException {

          // Construct an input source from the reader
          InputSource input = new InputSource(r);
          // Construct a new DOM parser
          DOMParser xp = new  DOMParser();
          // Parse in non-validating mode
          // xp.setValidationMode(false);
          // Preserve whitespace
          xp.setPreserveWhitespace(true);
          // Attempt to parse XML coming from the Reader
          xp.parse(input);
          return  xp.getDocument();
      }
      // ===============================================
      // Parse an XML document from an input stream
      // ===============================================
      public static XMLDocument parse(InputStream is, URL baseurl)
                  throws IOException, SAXParseException, SAXException {
          // Construct a reader and call parse(Reader)
          return parse(new InputStreamReader(is), baseurl);
      }      
      public static XMLDocument parse(InputStream is)
                  throws IOException, SAXParseException, SAXException {
          // Construct a reader and call parse(Reader)
          return parse(new InputStreamReader(is));
      }      
      // ===============================================
      // Parse an XML document from String
      // ===============================================
      public static XMLDocument parse(String xml, URL baseurl)
                  throws MalformedURLException, IOException, SAXParseException, SAXException {
          // Construct a reader and call parse(Reader)
          return parse(new StringReader(xml), baseurl);
      }      
      public static XMLDocument parse(String xml)
                  throws MalformedURLException, IOException, SAXParseException, SAXException {
          // Construct a reader and call parse(Reader)
          return parse(new StringReader(xml));
      }        
      // ===============================================
      // Parse an XML document from a URL
      // ===============================================
      public static XMLDocument parse(URL url, URL baseurl)
                  throws IOException, SAXParseException, SAXException {
          // Construct a reader and call parse(Reader)
          return parse(url.openStream(), baseurl);
      }      
      public static XMLDocument parse(URL url)
                  throws IOException, SAXParseException, SAXException {
          // Construct a reader and call parse(Reader)
          return parse(url.openStream());
      }      
      // ===============================================
      // Format information for a parse error
      // ===============================================
      public static String formatParseError(SAXParseException s) {
        int lineNum = s.getLineNumber();
        int colNum = s.getColumnNumber();
        String file = s.getSystemId();
        String err = s.getMessage();
        return "XML Parse error : " + (file != null ? "in file " + file : "")
                                    + " at line " + lineNum + " column " + colNum +
                                    " : " + err;
      }
      // ===============================================
      // Build a well formed URL
      // ===============================================
      public static URL newURL(String filename) throws MalformedURLException {
          URL url = null;
          try {
               // Check we don't have an already valid URL
               url = new URL(filename);
          }
          catch(MalformedURLException mue) {
               // get file's absolute path
               String path = (new File(filename)).getAbsolutePath();
               // If directory separator is not a forward slash, make it so
               if(File.separatorChar != '/') {
                    path = path.replace(File.separatorChar, '/');
               }
               // Add a leading slash if not present
               if(!path.startsWith("/")) path = "/" + path;
               url = new URL("file://" + path);
          }
          return url;
      }
}


