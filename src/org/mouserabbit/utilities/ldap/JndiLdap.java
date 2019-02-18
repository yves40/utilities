/*--------------------------------------------------------------------------
	JndiLdap.java
	C	Jul 18 2000
	U	Feb 03 2004  .11


     Jul 18 2000    Initial.
     Aug 13 2000    Add a getError() method.
     Sep 22 2000    Add a few methods.
     Aug 18 2001    Insert in the utilities package.
     Aug 29 2001    Authenticate opens AND closes a connection.
     Sep 01 2001    Add an empty constructor
     Dec 28 2001    Add an authenticate method
     Mar 06 2002    integrated into CVS
     Nov 02 2003    Package changed
     Nov 24 2003    Removed disconnection from Authenticate method
     Feb 03 2004    Add a method to get the directory context
     Feb 18 2019    VScode integration
--------------------------------------------------------------------------*/
package org.mouserabbit.utilities.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

/*--------------------------------------------------------------------------

--------------------------------------------------------------------------*/
public class JndiLdap
{
     static final String      Version = "JndiLdap V 1.13 Feb 03 2004";
     private String           servername;
     private int              port;
     private String           username;
     private String           password;
     private boolean          connected = false;
     private DirContext       ctx;
     private String           lasterror;
     private final String     defaultsearchfilter = "objectclass=*";

     //=========================================================================
     // LDAP connection
     //=========================================================================
     public JndiLdap() {}
     public JndiLdap(String servername, int port,
                              String username, String password) throws Exception {

          this.servername = servername;
          this.port = port;
          this.username = username;
          this.password = password;
     }
     //=========================================================================
     // Some public methods
     //=========================================================================
     public void Authenticate() throws Exception { openConnection(); }
     public void Authenticate(String user, String password) throws Exception { 
          this.username = user;
          this.username = password;
          openConnection(); 
     }
     //=========================================================================
     // Open the connection
     //=========================================================================
     public void openConnection() throws Exception {
          try {
               // Initiate the anonymous LDAP connection
               Hashtable env = new Hashtable();
               env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
               env.put(Context.PROVIDER_URL, "ldap://" + servername + ":" + port);
               if(username != null){          // Username specified ?
                    env.put(Context.SECURITY_AUTHENTICATION, "simple");
                    env.put(Context.SECURITY_PRINCIPAL, username);
                    env.put(Context.SECURITY_CREDENTIALS, password);
               }
               ctx = new InitialDirContext(env);
               connected = true;
          }
          catch (Exception e) { 
              connected = false; 
              lasterror = e.getMessage();
              throw new Exception("Unable to connect to " + servername + 
                         "<< " + e.getMessage() + " >>");
          }
     }
     //=========================================================================
     // Few set get methods
     //=========================================================================
     public void setUsername(String username) { this.username = username; }
     public void setPassword(String password) { this.password = password; }
     public void setServer(String servername) { this.servername = servername; }
     public void setPort(int port) { this.port = port; }
     
     public String getUser() { return username; }
     public String getPassword() { return password; }
     public String getServer() { return servername; }
     public int  getPort() { return port; }
     //=========================================================================
     // Close the established connection
     //=========================================================================
     public void closeConnection() throws Exception {
          try {
               ctx.close();
               connected = false;
          }
          catch(Exception e) { 
              connected = false; 
              lasterror = e.getMessage();
              throw new Exception("Unable to disconnect from  " + servername + 
                         "<< " + e.getMessage() + " >>");
          }
     }
     //=========================================================================
     // Get the connected state of the ldap object
     //=========================================================================
     public boolean getConnectionState() { return connected; }
     //=========================================================================
     // Get the Dircontext object
     //=========================================================================
     public DirContext getDirContext() { return ctx; }
     //=========================================================================
     // Get the last error encountered
     //=========================================================================
     public String getLastError() { return lasterror; }
     //=========================================================================
     // Get entries under a specific dn.
     // Scope is specified, as well as attributes to be retrieved
     //=========================================================================
     public NamingEnumeration getEntries(String dn, int searchscope)
          throws NamingException {
          SearchControls controls = new SearchControls();
          controls.setSearchScope(searchscope);
          NamingEnumeration answer = ctx.search(dn, defaultsearchfilter, controls);
          return answer;
     }
}

