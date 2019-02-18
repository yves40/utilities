/*------------------------------------------------------------------------------------------
    DBPoolSingleton.java

    Aug 09 2010    Initial.
    Aug 11 2010    Add methods. 1st working version
                   Additional JDBC pool parameters got from the XML file
    Aug 15 2010    Multipool
    Aug 17 2010    Work on ASO network encryption
    Aug 18 2010    Work on ASO network encryption. Not working ;-(
    Aug 23 2010    ASO network encryption. Fixing problems
    Aug 27 2010    Fix some programming logic error...
    Aug 27 2010    Fix some programming logic error...
    Sep 01 2010    Exploit the <secure active="true"> fully
    Sep 21 2014    Integrate in the loadtool project
    Feb 18 2019    VScode integration
--------------------------------------------------------------------------------------*/
package org.mouserabbit.utilities.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLNode;

import org.apache.log4j.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DBPoolSingleton {

  static final String Version = "DBPoolSingleton, V 1.58 OCt 21 2014 ";
  private static DBPoolSingleton thisInstance = null;
  private OracleConnectionCacheManager connMgr = null;
  protected static OracleDataSource _ods = null; // Oracle pool data source
  static Logger _RootLogger = Logger.getRootLogger();
  static Logger _log = _RootLogger.getLogger(DBPoolSingleton.class.getName());

  private String _user = null;
  private String _password = null;
  private String _servicename = null;
  private String _server = null;
  private String _port = null;
  private String _minlimit = null;
  private String _maxlimit = null;
  private String _networkprotocol = null;
  private String _cachename = null;
  private Hashtable _datasources;

  // -------------------------------------------------------
  // Constructor, private for singleton use
  // -------------------------------------------------------
  private DBPoolSingleton() throws Exception {
  }

  private DBPoolSingleton(XMLDocument xmldoc) throws Exception {
    if (_ods == null) {
      _log.info(Version + "Pool singleton constructor called");
      _datasources = new Hashtable();
      initializeConnectionCache(xmldoc);
    }
  }

  // -------------------------------------------------------
  // The getInstance method
  // Receives an xml document for connection properties
  // Called by a main thread to initialize the pool
  // -------------------------------------------------------
  public static DBPoolSingleton getInstance(XMLDocument xmldoc) throws Exception {
    if (thisInstance == null) {
      thisInstance = new DBPoolSingleton(xmldoc);
    }
    return thisInstance;
  }

  // -------------------------------------------------------
  // The getInstance method
  // Called once the pool has been initialized
  // -------------------------------------------------------
  public static DBPoolSingleton getInstance() throws Exception {
    if (thisInstance == null) {
      _log.error(Version + "Pool is not initialized, cannot proceed");
      throw new Exception("Cannot return an uninitialized pool");
    }
    return thisInstance;
  }

  // -------------------------------------------------------
  // Connection cache creation stuff
  // -------------------------------------------------------
  private void initializeConnectionCache(XMLDocument xmldoc) throws Exception {

    NodeList jdbcnode = null;
    XMLNode node = null;
    XMLNode innernode = null;
    NamedNodeMap nnp = null;

    try {
      /* Initialize the Connection Cache */
      connMgr = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
      /*-------------------------------------------------------
       * Parse the XML file to get connection properties
       -------------------------------------------------------*/
      jdbcnode = xmldoc.selectNodes("/runprofile/jdbcpool");
      if ((jdbcnode == null) | (jdbcnode.getLength() == 0))
        throw new Exception("No JDBC");
      boolean cacheactive = true;
      for (int i = 0; i < jdbcnode.getLength(); ++i) {
        cacheactive = true; // The default
        node = (XMLNode) jdbcnode.item(i);
        /*-------------------------------------------------------
         * Get the cache name
         -------------------------------------------------------*/
        nnp = node.getAttributes();
        innernode = (XMLNode) nnp.getNamedItem("name");
        if (innernode == null) {
          throw new Exception("An XML pool node must have a name item: Check you XMLfile.");
        }
        _cachename = innernode.getText();
        if (_cachename == null) {
          throw new Exception("An XML pool name item must have a value: Check you XMLfile.");
        }
        innernode = (XMLNode) nnp.getNamedItem("active");
        if (innernode != null) {
          if (innernode.getText().equalsIgnoreCase("false")) {
            _log.info(Version + "Pool " + _cachename + " desactivated");
            cacheactive = false;
          }
        }
        /*-------------------------------------------------------
         * Now proceed to pool initialization
         -------------------------------------------------------*/
        if (cacheactive) {
          _ods = new OracleDataSource();
          _user = node.selectSingleNode("user").getFirstChild().getNodeValue();
          _password = node.selectSingleNode("password").getFirstChild().getNodeValue();
          _servicename = node.selectSingleNode("servicename").getFirstChild().getNodeValue();
          _server = node.selectSingleNode("server").getFirstChild().getNodeValue();
          _port = node.selectSingleNode("port").getFirstChild().getNodeValue();
          _minlimit = node.selectSingleNode("minlimit").getFirstChild().getNodeValue();
          _maxlimit = node.selectSingleNode("maxlimit").getFirstChild().getNodeValue();
          _networkprotocol = node.selectSingleNode("networkprotocol").getFirstChild().getNodeValue();

          Properties poolprop = new Properties();
          poolprop.setProperty("MinLimit", _minlimit); // the cache size is 5 at least
          poolprop.setProperty("MaxLimit", _maxlimit);
          poolprop.setProperty("networkProtocol", _networkprotocol);
          /*
           * Do we need to set up some encryption parameters ?
           */
          EvaluateEncryptionSpecifications(node, poolprop, _cachename);

          _ods.setUser(_user);
          _ods.setPassword(_password);
          _ods.setServerName(_server);
          _ods.setPortNumber(Integer.parseInt(_port));
          _ods.setServiceName(_servicename);
          _ods.setDriverType("thin");
          /*
           * Beware, the order of these instructions is important
           */
          _ods.setConnectionCacheProperties(poolprop);
          _ods.setConnectionCachingEnabled(true); // Enable caching
          _ods.setConnectionProperties(poolprop);
          _ods.setConnectionCacheName(_cachename);

          /*
           * Preload the pool by opening the minimu number of connections
           */

          try {
            _log.info(Version + "Preloading " + _minlimit + " minimum connections in pool " + _cachename);
            for (int loop = 0; loop < Integer.parseInt(_minlimit); ++loop) {
              Connection conn = _ods.getConnection(poolprop);
              Statement stmt = conn.createStatement();
              ResultSet rset = stmt.executeQuery("select user from dual");
              rset.close();
              conn.close();
            }
            this._datasources.put(_cachename, _ods); // Push this new connection pool in the named cache pool
            if (i == 0) {
              this._datasources.put("default", _ods); // For the 1st one, tag is as default
            }
            _log.info(Version + "Pool activated");
            _log.debug(Version + "The activated cache name is : " + _cachename);

            /*
             * Reservoir code...
             */
            poolprop = connMgr.getCacheProperties(_cachename);
            Enumeration allproperties = poolprop.keys();
            String propname, propvalue = null;
            while (allproperties.hasMoreElements()) {
              propname = (String) allproperties.nextElement();
              propvalue = poolprop.getProperty(propname);
              _log.debug(Version + "Property : " + propname + " value : " + propvalue);
            }
          } catch (Exception e) {
            _log.error(Version + "Crash during jdbc pool creation / checkup : " + e.getMessage());
          }
        }
      }
    } catch (Exception e) {
      _log.error(Version + "Connection cache creation error:" + e.getMessage());
    }
  }

  /*
   * /------------------------------------------------------- Security
   * specifications evaluation <secure active="true">
   * <encryptionlevel>REQUIRED</encryptionlevel>
   * <encryptiontype>AES256</encryptiontype>
   * <checksumlevel>REQUIRED</checksumlevel> <checksumtype>SHA1</checksumtype>
   * </secure> -------------------------------------------------------
   */
  protected void EvaluateEncryptionSpecifications(XMLNode jdbcnode, Properties poolprop, String cachename)
      throws Exception {

    Node securenode, paramnode = null;
    XMLNode innernode = null;
    NamedNodeMap nnp = null;
    String enctype = null, enclevel = null, checksumtype = null, checksumlevel = null;
    int parameternumber = 0;

    securenode = jdbcnode.selectSingleNode("secure");
    nnp = securenode.getAttributes();
    innernode = (XMLNode) nnp.getNamedItem("active");
    if (innernode == null) {
      throw new Exception("Cannot find the mandatory active attribute from the <secure> node");
    }

    String status = innernode.getText();
    if (status.isEmpty()) {
      _log.error("Unable to get the secure parameters status. Should be true of false under the active attribute.");
      throw new Exception(
          "Unable to get the secure parameters status. Should be true of false under the active attribute.");
    }
    if (status.toUpperCase().equals("TRUE")) {
      NodeList nl = securenode.getChildNodes();
      for (int j = 0; j < nl.getLength(); ++j) {
        paramnode = nl.item(j);
        String paramname = paramnode.getNodeName();
        if (paramname.toLowerCase().toLowerCase().equals("encryptionlevel")) {
          ++parameternumber;
          enclevel = paramnode.getTextContent();
          poolprop.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_ENCRYPTION_LEVEL, enclevel);
        }
        if (paramname.toLowerCase().toLowerCase().equals("encryptiontype")) {
          ++parameternumber;
          enctype = paramnode.getTextContent();
          poolprop.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_ENCRYPTION_TYPES, enctype);
        }
        if (paramname.toLowerCase().toLowerCase().equals("checksumlevel")) {
          ++parameternumber;
          checksumlevel = paramnode.getTextContent();
          poolprop.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CHECKSUM_LEVEL, checksumlevel);
        }
        if (paramname.toLowerCase().toLowerCase().equals("checksumtype")) {
          ++parameternumber;
          checksumtype = paramnode.getTextContent();
          poolprop.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CHECKSUM_TYPES, checksumtype);
        }
      }
      if (parameternumber != 4) {
        _log.error("Please provide all four parameters for the secure connection. See the XML template for details.");
        throw new Exception(
            "Please provide all four parameters for the secure connection. See the XML template for details.");
      } else {
        _log.info(Version + "Network security requested for pool " + cachename);
        _log.info(
            "          ENCRYPTION: " + enctype + "/" + enclevel + " CHECKSUM: " + checksumtype + "/" + checksumlevel);
      }
    }
  }

  // -------------------------------------------------------
  // Get a connection from the pool
  // The default one, no name specified
  // -------------------------------------------------------
  public Connection getConnection() throws Exception {
    try {
      Connection conn = ((OracleDataSource) _datasources.get("default")).getConnection();
      return conn;
    } catch (Exception e) {
      throw new Exception("Unable to retrieve a connection from the pool");
    }
  }

  // -------------------------------------------------------
  // Get a named connection from the pool
  // -------------------------------------------------------
  public Connection getConnection(String name) throws Exception {
    try {
      Connection conn = ((OracleDataSource) _datasources.get(name)).getConnection();
      return conn;
    } catch (Exception e) {
      throw new Exception("Unable to retrieve [" + name + "] connection from the pool ");
    }
  }

  // -------------------------------------------------------
  // Release a connection from the pool
  // -------------------------------------------------------
  public void releaseConnection(Connection conn) {
    try {
      conn.close();
    } catch (Exception e) {
      _log.error(Version + "A problem occurred when closing the connection : " + e.getMessage());
    }
  }

  // -------------------------------------------------------
  // Get the datasource
  // -------------------------------------------------------
  public OracleDataSource getDataSource() {
    if (_ods != null) {
      return _ods;
    } else {
      _log.error(Version + "Cannot get an initialized datasource.");
      return null;
    }
  }
}
